package com.camunda.handles.ProgramarProducao;

import com.camunda.classes.Cliente;
import com.camunda.classes.ProgramarProducao.Order;
import com.camunda.classes.ProgramarProducao.OrderDescription;
import com.camunda.classes.RegistoLote.Enums.TypePizza;
import com.camunda.utils.LoteUtils;
import com.camunda.utils.OrdersUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterOrderHandle implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: SOLICITAR ENCOMENDAS] A processar dados do formulário...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            // 1. LER DADOS DO FORMULÁRIO
            String cName = (String) variables.get("clientName");
            String cEmail = (String) variables.get("clientEmail");
            String pTypeStr = (String) variables.get("pizzaType");

            // O Camunda pode enviar números como Integer ou Double
            Number qtyNum = (Number) variables.get("quantity");
            int quantity = (qtyNum != null) ? qtyNum.intValue() : 1;

            // Validação de segurança (caso venha vazio)
            if (cName == null || pTypeStr == null) {
                System.out.println("   [!] Aviso: Campos vazios. A usar valores de teste.");
                cName = (cName == null) ? "Cliente Balcão" : cName;
                pTypeStr = (pTypeStr == null) ? "PEPPERONI" : pTypeStr;
            }

            // 2. CRIAR OBJETOS JAVA
            String clientId = "CLI-" + UUID.randomUUID().toString().substring(0, 5);
            Cliente cliente = new Cliente(clientId, cName);
            cliente.setMail(cEmail);

            // Determinar Tipo de Pizza
            TypePizza typePizza;
            try {
                typePizza = TypePizza.valueOf(pTypeStr);
            } catch (IllegalArgumentException e) {
                System.out.println("   [!] Tipo desconhecido: " + pTypeStr + ". A assumir PEPPERONI.");
                typePizza = TypePizza.PEPPERONI;
            }

            // Criar Item do Pedido
            OrderDescription item = new OrderDescription(typePizza, quantity);

            // Criar a Encomenda Completa
            String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
            Order order = new Order(
                    orderId,
                    LocalDate.now().toString(),
                    "PENDING",
                    cliente,
                    item
            );

            System.out.println("   > ID Gerado: " + orderId);
            System.out.println("   > Cliente: " + cName);
            System.out.println("   > Pedido: " + quantity + "x " + typePizza);

            OrdersUtils.saveOrderToDisk(order);
            // 3. PREPARAR SAÍDA
            Map<String, Object> output = new HashMap<>();

            // Guardamos o objeto completo em 'orderData' para as próximas tarefas usarem
            output.put("orderData", order);
            output.put("orderId", orderId);

            client.newCompleteCommand(job.getKey())
                    .variables(output)
                    .send()
                    .join();

            System.out.println(">>> Encomenda registada com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao ler formulário: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
