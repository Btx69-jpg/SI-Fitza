package com.camunda.handles.ProgramarProducao;

import com.camunda.classes.Cliente;
import com.camunda.classes.ProgramarProducao.Order;
import com.camunda.classes.ProgramarProducao.OrderDescription;
import com.camunda.classes.RegistoLote.Enums.TypePizza;
import com.camunda.utils.LoteUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GetProductOrderDetailsHandle implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: REGISTO ENCOMENDA] A processar dados do formulário...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            // 1. Ler as variáveis INDIVIDUAIS que vêm do Formulário
            // Nota: Os nomes aqui ("clientName", "pizzaType"...) têm de ser iguais aos 'Keys' do formulário
            String cName = (String) variables.get("clientName");
            String cEmail = (String) variables.get("clientEmail");
            String pTypeStr = (String) variables.get("pizzaType");

            // O Camunda pode enviar números como Integer ou Double, por isso usamos Number para ser seguro
            Number qtyNum = (Number) variables.get("quantity");
            int quantity = (qtyNum != null) ? qtyNum.intValue() : 1;

            // Validação simples (para evitar erros se o form vier vazio)
            if (cName == null || pTypeStr == null) {
                System.out.println("   [!] Aviso: Dados incompletos no formulário. A usar valores de teste.");
                cName = (cName == null) ? "Cliente Teste" : cName;
                pTypeStr = (pTypeStr == null) ? "PEPPERONI" : pTypeStr;
            }

            // 2. Criar os Objetos Java
            // Criar Cliente
            String clientId = "CLI-" + UUID.randomUUID().toString().substring(0, 5);
            Cliente cliente = new Cliente(clientId, cName);
            cliente.setMail(cEmail);

            // Converter String do formulário para o Enum TypePizza
            TypePizza typePizza;
            try {
                typePizza = TypePizza.valueOf(pTypeStr);
            } catch (IllegalArgumentException e) {
                System.out.println("   [!] Tipo de pizza desconhecido: " + pTypeStr + ". A assumir PEPPERONI.");
                typePizza = TypePizza.PEPPERONI;
            }

            // Criar a descrição do item
            OrderDescription item = new OrderDescription(typePizza, quantity);

            // Criar a Encomenda (Order) completa
            String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
            Order order = new Order(
                    orderId,
                    LocalDate.now().toString(),
                    "PENDING",
                    cliente,
                    item
            );

            System.out.println("   > Encomenda Gerada: " + orderId);
            System.out.println("   > Cliente: " + cName + " (" + cEmail + ")");
            System.out.println("   > Pedido: " + quantity + "x " + typePizza);

            // 3. Preparar as variáveis de saída
            Map<String, Object> output = new HashMap<>();

            // AQUI ESTÁ O TRUQUE: Guardamos o objeto 'order' na variável 'orderData'
            // Assim, o resto dos workers (que esperam 'orderData') vão funcionar sem problemas.
            output.put("orderData", order);

            // Também enviamos o ID solto, dá jeito para emails e pesquisas
            output.put("orderId", orderId);

            // 4. Completar a tarefa
            client.newCompleteCommand(job.getKey())
                    .variables(output)
                    .send()
                    .join();

            System.out.println(">>> Dados processados e convertidos com sucesso.");

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao processar formulário: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}

