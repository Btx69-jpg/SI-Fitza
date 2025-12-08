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

/**
 * {@code GetProductOrderDetailsHandle} é um {@link io.camunda.zeebe.client.api.worker.JobHandler}
 * responsável por **receber dados brutos** de uma fonte (como um Formulário Camunda Tasklist
 * ou variáveis soltas de uma mensagem) e **converter/estruturar esses dados**
 * nos objetos Java de negócio esperados ({@link Cliente}, {@link OrderDescription},
 * e o objeto principal {@link Order}).
 *
 * <p>Esta é tipicamente a primeira tarefa de serviço após a receção de um pedido
 * via formulário de utilizador ou receção de mensagem.
 *
 * <p>A principal variável de saída é {@code orderData} (objeto {@link Order} serializado).
 */
public class GetProductOrderDetailsHandle implements JobHandler {

    /**
     * Trata a tarefa (Job) ativada do Camunda Zeebe.
     *
     * <p>O fluxo de trabalho principal é:
     * <ol>
     * <li>Obter variáveis soltas ({@code clientName}, {@code pizzaType}, {@code quantity}, etc.) do {@code ActivatedJob}.</li>
     * <li>Realizar validações básicas e conversões (ex: String para {@code TypePizza}, Number para int).</li>
     * <li>Criar instâncias dos objetos de negócio ({@link Cliente}, {@link OrderDescription}, {@link Order}), gerando IDs únicos.</li>
     * <li>Serializar o objeto {@code Order} completo e enviá-lo de volta ao processo como a variável {@code orderData}.</li>
     * </ol>
     *
     * @param client O cliente do Job para enviar comandos de conclusão ({@code complete}) ou falha ({@code fail}).
     * @param job O Job ativado que contém os detalhes da tarefa e variáveis de entrada.
     * @throws Exception Se ocorrer um erro durante o processamento.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: REGISTO ENCOMENDA] A processar dados do formulário...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            //Ler as variáveis INDIVIDUAIS que vêm do Formulário
            String cName = (String) variables.get("clientName");
            String cEmail = (String) variables.get("clientEmail");
            String pTypeStr = (String) variables.get("pizzaType");

            Number qtyNum = (Number) variables.get("quantity");
            int quantity = (qtyNum != null) ? qtyNum.intValue() : 1;

            // Validação simples (para evitar erros se o form vier vazio)
            if (cName == null || pTypeStr == null) {
                System.out.println("   [!] Aviso: Dados incompletos no formulário. A usar valores de teste.");
                cName = (cName == null) ? "Cliente Teste" : cName;
                pTypeStr = (pTypeStr == null) ? "PEPPERONI" : pTypeStr;
            }

            //Criar os Objetos Java
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

            //Criar a Encomenda (Order) completa
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

            //Preparar as variáveis de saída
            Map<String, Object> output = new HashMap<>();

            output.put("orderData", order);
            output.put("orderId", orderId);

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

