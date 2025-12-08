package com.camunda.handles.ProgramarProducao;
import com.camunda.utils.SendEmailUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;

public class ContactMaintenanceEmailHandle implements JobHandler {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    @Override
    public void handle(JobClient client, ActivatedJob job) {
        System.out.println("\n>>> [EMAIL] A notificar equipa de manutenção sobre falha...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            //variaveis: id da maquina e detalhes do erro
            String machineId = (String) variables.getOrDefault("machineId", "Máquina Desconhecida");
            String errorDetails = (String) variables.getOrDefault("errorDetails", "Nenhum detalhe técnico fornecido.");

            String destinatario = dotenv.get("EMAIL_MANUTENCAO");

            String assunto = "URGENTE: Falha Crítica na " + machineId;

            String corpo = String.format(
                    "Equipa de Manutenção,\n\n" +
                            "Foi detetada uma falha que interrompeu a produção.\n\n" +
                            "--- Detalhes do Incidente ---\n" +
                            "Máquina: %s\n" +
                            "Descrição do Erro: %s\n" +
                            "-----------------------------\n\n" +
                            "Aguardamos intervenção imediata.\n\n" +
                            "Sistema de Controlo de Produção",
                    machineId, errorDetails
            );

            SendEmailUtils.sendEmail(destinatario, assunto, corpo);
            System.out.println(">>> Email de manutenção enviado para: " + destinatario);

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao contactar manutenção: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}