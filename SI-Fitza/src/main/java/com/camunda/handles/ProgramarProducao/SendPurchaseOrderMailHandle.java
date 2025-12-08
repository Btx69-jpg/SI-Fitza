package com.camunda.handles.ProgramarProducao;

import com.camunda.utils.SendEmailUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;

/**
 * {@code SendPurchaseOrderMailHandle} é um {@link io.camunda.zeebe.client.api.worker.JobHandler}
 * responsável por **enviar um e-mail de Pedido de Reposição (Purchase Order)**
 * para o fornecedor quando o stock necessário não está disponível.
 *
 * <p>Esta classe lê as variáveis de processo ({@code materialName} e {@code quantityRequired})
 * e utiliza a variável de ambiente {@code EMAIL_FORNECEDOR} (carregada via Dotenv)
 * como destinatário para simular o pedido de compra.
 *
 * <p>Utiliza a utilidade {@link SendEmailUtils} para simular o envio de e-mail.
 */
public class SendPurchaseOrderMailHandle implements JobHandler {

    /**
     * Objeto Dotenv estático utilizado para carregar variáveis de ambiente,
     * como o endereço de e-mail do fornecedor ({@code EMAIL_FORNECEDOR}).
     */
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    /**
     * Trata a tarefa (Job) ativada do Camunda Zeebe.
     *
     * <p>O fluxo de trabalho é:
     * <ol>
     * <li>Obter variáveis de processo ({@code materialName}, {@code quantityRequired}).</li>
     * <li>Obter o endereço de e-mail do fornecedor das variáveis de ambiente.</li>
     * <li>Montar o assunto e o corpo do e-mail com os dados da reposição.</li>
     * <li>Chamar {@link SendEmailUtils#sendEmail(String, String, String)} para simular o envio.</li>
     * <li>Completar a tarefa.</li>
     * </ol>
     *
     * @param client O cliente do Job para enviar comandos de conclusão ({@code complete}) ou falha ({@code fail}).
     * @param job O Job ativado que contém os detalhes da tarefa e variáveis de entrada.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) {
        System.out.println("\n>>> [EMAIL] A preparar pedido de reposição de stock...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            String materialName = (String) variables.getOrDefault("materialName", "Material Não Especificado");

            Object qtyObj = variables.get("quantityRequired");
            String quantity = (qtyObj != null) ? qtyObj.toString() : "0";

            String destinatario = dotenv.get("EMAIL_FORNECEDOR");

            String assunto = "Pedido de Reposição: " + materialName;

            // Corpo do email
            String corpo = String.format(
                    "Prezados,\n\n" +
                            "Solicitamos a reposição urgente do seguinte item de stock:\n\n" +
                            " - Material: %s\n" +
                            " - Quantidade Necessária: %s unidades\n\n" +
                            "Por favor, confirmar data de entrega.\n\n" +
                            "Atentamente,\n" +
                            "Gestão de Stock",
                    materialName, quantity
            );

            // envia o email
            SendEmailUtils.sendEmail(destinatario, assunto, corpo);

            System.out.println(">>> Email de reposição enviado para: " + destinatario);

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Falha ao enviar email de stock: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}