package com.camunda.handles.ProgramarProducao;

import com.camunda.utils.SendEmailUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;

public class SendPurchaseOrderMailHandle implements JobHandler {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    @Override
    public void handle(JobClient client, ActivatedJob job) {
        System.out.println("\n>>> [EMAIL] A preparar pedido de reposição de stock...");

        try {
            // Obter as variáveis do processo (Assumi que os materiais que faltavam ja foram calculados e guardados)
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