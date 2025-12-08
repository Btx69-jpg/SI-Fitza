package com.camunda.handles.ProgramarProducao;

import com.camunda.utils.SendEmailUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.Map;

public class NotifyClientDeadlineEmailHandle implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) {
        System.out.println("\n>>> [EMAIL] A notificar cliente sobre prazo de entrega...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            //Tentar obter dados diretos (se você fez Input Mapping no BPMN)
            String clientName = (String) variables.get("clientName");
            String clientEmail = (String) variables.get("clientEmail");

            // Se não vieram mapeados, tentar extrair do objeto 'orderData'
            if (clientEmail == null || clientName == null) {
                Map<String, Object> orderData = (Map<String, Object>) variables.get("orderData");
                if (orderData != null) {
                    Map<String, Object> clientData = (Map<String, Object>) orderData.get("ClientData");
                    if (clientData != null) {
                        clientName = (String) clientData.getOrDefault("clienteName", "Cliente");
                        clientEmail = (String) clientData.getOrDefault("mail", "");
                    }
                }
            }

            // Obter a Data de Entrega ( criada anteriormente no processo)
            String deliveryDate = (String) variables.getOrDefault("deliveryDate", "A definir");
            String orderId = (String) variables.getOrDefault("orderId", (String) variables.getOrDefault("correlationKey", "N/A"));

            // 4. Montar o Email
            String assunto = "Previsão de Entrega - Pedido " + orderId;

            String corpo = String.format(
                    "Olá %s,\n\n" +
                            "Temos o prazer de informar que o seu pedido (%s) foi processado.\n\n" +
                            "Data Estimada de Entrega: %s\n\n" +
                            "Caso esta data não seja conveniente, por favor entre em contacto connosco.\n\n" +
                            "Obrigado pela preferência,\n" +
                            "Equipa de Produção",
                    clientName, orderId, deliveryDate
            );

            SendEmailUtils.sendEmail(clientEmail, assunto, corpo);

            System.out.println(">>> Notificação de prazo enviada para: " + clientEmail);

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao notificar cliente: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}