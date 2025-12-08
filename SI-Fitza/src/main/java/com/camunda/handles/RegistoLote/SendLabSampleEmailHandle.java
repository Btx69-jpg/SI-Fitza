package com.camunda.handles.RegistoLote;

import com.camunda.utils.SendEmailUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;

/**
 * Worker do Camunda/Zeebe responsável por **enviar uma notificação por email**
 * ao laboratório, indicando que uma amostra de um determinado Lote está pronta
 * para ser recolhida e analisada.
 * <p>
 * Este Worker utiliza a utilitária {@link SendEmailUtils} para a comunicação SMTP
 * e obtém o endereço do destinatário a partir de variáveis de ambiente.
 * </p>
 *
 * <h3>Requisitos:</h3>
 * <ul>
 * <li>A variável de processo {@code "loteId"} deve estar presente para ser incluída no assunto e corpo do email.</li>
 * <li>A variável de ambiente {@code EMAIL_LABORATORIO} deve estar configurada no ficheiro {@code .env} para identificar o destinatário.</li>
 * </ul>
 */
public class SendLabSampleEmailHandle implements JobHandler {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    /**
     * Lógica de tratamento para o Job, responsável por construir e enviar o email de notificação.
     *
     * @param client O cliente do Job, usado para completar ou falhar o Job.
     * @param job O Job ativado pelo motor Zeebe.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) {
        System.out.println("\n>>> [EMAIL] A preparar envio de notificação para o laboratório...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            String loteId = (String) variables.getOrDefault("loteId", "DESCONHECIDO");

            String destinatario = dotenv.get("EMAIL_LABORATORIO");
            String assunto = "Envio de amostra do Lote: " + loteId;

            String corpo = String.format(
                    "Bom dia,\n\nO Lote %s está pronto para recolha de amostras.\n" +
                            "Por favor procedam à análise.\n\nCumprimentos,\nProdução",
                    loteId
            );

            SendEmailUtils.sendEmail(destinatario, assunto, corpo);

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro fatal de email: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
