package com.camunda.handles.RegistoLote;

import com.camunda.utils.SendEmailUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;

public class SendLabSampleEmailHandle implements JobHandler {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

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
        } catch (Exception e) {
            e.printStackTrace();
            // Se o email falhar, podes querer tentar de novo ou falhar o job
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Falha no envio de email: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
