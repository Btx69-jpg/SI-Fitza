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

/**
 * {@code RegisterOrderHandle} é um {@link io.camunda.zeebe.client.api.worker.JobHandler}
 * responsável por **receber os dados de uma nova encomenda** (tipicamente de um
 * formulário ou API), **estruturá-los** nos objetos de negócio ({@link Order}) e
 * **persistir a encomenda** (simulada via {@link OrdersUtils#saveOrderToDisk(Order)}).
 *
 * <p>Esta classe realiza a validação de tipos, gera IDs únicos para o cliente e
 * para a encomenda, e envia o objeto {@code Order} completo de volta ao processo
 * na variável {@code orderData}.
 */
public class RegisterOrderHandle implements JobHandler {

    /**
     * Trata a tarefa (Job) ativada do Camunda Zeebe.
     *
     * <p>O fluxo de trabalho principal é:
     * <ol>
     * <li>Obter variáveis soltas ({@code clientName}, {@code pizzaType}, etc.) do {@code ActivatedJob}.</li>
     * <li>Converter e validar os dados de entrada, aplicando valores de teste se necessário.</li>
     * <li>Gerar IDs únicos para o cliente e a encomenda.</li>
     * <li>Criar os objetos de negócio {@link Cliente}, {@link OrderDescription} e o objeto {@link Order} principal.</li>
     * <li>Chamar a utilidade {@link OrdersUtils#saveOrderToDisk(Order)} para simular o registo.</li>
     * <li>Completar a tarefa, enviando o objeto {@code Order} completo na variável {@code orderData}.</li>
     * </ol>
     *
     * @param client O cliente do Job para enviar comandos de conclusão ({@code complete}) ou falha ({@code fail}).
     * @param job O Job ativado que contém os detalhes da tarefa e variáveis de entrada.
     * @throws Exception Se ocorrer um erro durante o processamento ou persistência dos dados.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: SOLICITAR ENCOMENDAS] A processar dados do formulário...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            //LER DADOS DO FORMULÁRIO
            String cName = (String) variables.getOrDefault("clienteName", variables.get("clientName"));
            String cEmail = (String) variables.get("clientEmail");
            String pTypeStr = (String) variables.get("pizzaType");

            Number qtyNum = (Number) variables.get("quantity");
            int quantity = (qtyNum != null) ? qtyNum.intValue() : 1;

            String dateStr = (String) variables.get("orderDate");

            if (cName == null || pTypeStr == null) {
                System.out.println("   [!] Aviso: Campos vazios. A usar valores de teste.");
                cName = (cName == null) ? "Cliente Balcão" : cName;
                pTypeStr = (pTypeStr == null) ? "PEPPERONI" : pTypeStr;
            }

            String dateToUse;
            if (dateStr != null && !dateStr.isEmpty()) {
                dateToUse = dateStr;
            } else {
                dateToUse = LocalDate.now().toString();
                System.out.println("   [!] Data não fornecida. A usar data de hoje: " + dateToUse);
            }

            //CRIAR OBJETOS JAVA
            String clientId = "CLI-" + UUID.randomUUID().toString().substring(0, 5);
            Cliente cliente = new Cliente(clientId, cName);
            cliente.setMail(cEmail);

            TypePizza typePizza;
            try {
                typePizza = TypePizza.valueOf(pTypeStr);
            } catch (IllegalArgumentException e) {
                System.out.println("   [!] Tipo desconhecido: " + pTypeStr + ". A assumir PEPPERONI.");
                typePizza = TypePizza.PEPPERONI;
            }

            OrderDescription item = new OrderDescription(typePizza, quantity);
            String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
            Order order = new Order(
                    orderId,
                    dateToUse,
                    "PENDING",
                    cliente,
                    item
            );

            System.out.println("   > ID Gerado: " + orderId);
            System.out.println("   > Cliente: " + cName);
            System.out.println("   > Pedido: " + quantity + "x " + typePizza);

            OrdersUtils.saveOrderToDisk(order);

            //PREPARAR SAÍDA
            Map<String, Object> output = new HashMap<>();

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
