package com.camunda.handles.ProgramarProducao;

import com.camunda.utils.SendEmailUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.Map;

/**
 * {@code NotifyClientDeadlineEmailHandle} é um {@link io.camunda.zeebe.client.api.worker.JobHandler}
 * responsável por **enviar um e-mail de notificação ao cliente** informando-o
 * sobre o prazo de entrega estimado ({@code deliveryDate}) da sua encomenda.
 *
 * <p>Esta classe tenta extrair os dados do cliente (nome e e-mail) mapeados diretamente
 * nas variáveis do Job ou, em alternativa, navegando dentro do objeto {@code orderData}.
 *
 * <p>Utiliza a utilidade {@link SendEmailUtils} para simular o envio de e-mail.
 */
public class NotifyClientDeadlineEmailHandle implements JobHandler {

    /**
     * Trata a tarefa (Job) ativada do Camunda Zeebe.
     *
     * <p>O fluxo de trabalho é:
     * <ol>
     * <li>Obter variáveis necessárias: {@code clientName}, {@code clientEmail}, {@code deliveryDate} e {@code orderId}.</li>
     * <li>Implementar lógica de fallback para extrair dados do cliente a partir do objeto {@code orderData}
     * caso não estejam mapeados diretamente no Job.</li>
     * <li>Montar o assunto e o corpo do e-mail com os dados da encomenda e o prazo de entrega.</li>
     * <li>Chamar {@link SendEmailUtils#sendEmail(String, String, String)} para simular o envio.</li>
     * <li>Completar a tarefa.</li>
     * </ol>
     *
     * @param client O cliente do Job para enviar comandos de conclusão ({@code complete}) ou falha ({@code fail}).
     * @param job O Job ativado que contém os detalhes da tarefa e variáveis de entrada.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) {
        System.out.println("\n>>> [EMAIL] A notificar cliente sobre prazo de entrega...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            String clientName = (String) variables.get("clientName");
            String clientEmail = (String) variables.get("mail");

            if (clientEmail == null || clientName == null) {
                Map<String, Object> orderData = (Map<String, Object>) variables.get("orderData");

                if (orderData != null) {
                    Map<String, Object> clientDataObj = (Map<String, Object>) orderData.get("clientData");

                    if (clientDataObj != null) {
                        if (clientName == null) {
                            clientName = (String) clientDataObj.getOrDefault("clienteName", "Cliente");
                        }

                        if (clientEmail == null) {
                            clientEmail = (String) clientDataObj.get("mail");
                        }
                    }
                }
            }

            String deliveryDate = (String) variables.getOrDefault("orderDate", "A definir");
            String orderId = (String) variables.getOrDefault("orderId", (String) variables.getOrDefault("correlationKey", "N/A"));

            //Montar o Email
            String assunto = "Previsão de Entrega - Pedido";

            String corpo = String.format(
                    "Olá,\n\n" +
                            "Temos o prazer de informar que o seu pedido foi processado.\n\n" +
                            "Data Estimada de Entrega: %s\n\n" +
                            "Caso esta data não seja conveniente, por favor entre em contacto connosco.\n\n" +
                            "Obrigado pela preferência,\n" +
                            "Equipa de Produção",
                    deliveryDate
            );

            if(clientEmail == null) {
                clientEmail = "8230138@estg.ipp.pt";
            }

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