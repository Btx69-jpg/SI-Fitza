package com.camunda.handles.ProgramarProducao;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CalculateDeliveryDateHandle implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: ESTIMAR PRAZOS] A calcular data de entrega (Simulação)...");

        try {
            // Lógica: Gerar uma data aleatória entre Hoje e Hoje + 2 Semanas (14 dias)
            Random random = new Random();
            int daysToAdd = random.nextInt(15); // Gera um número de 0 a 14

            LocalDate estimatedDate = LocalDate.now().plusDays(daysToAdd);

            System.out.println("   > Previsão calculada: " + daysToAdd + " dias de produção.");
            System.out.println("   > Data de Entrega: " + estimatedDate.toString());

            // Enviar a variável 'deliveryDate' para o processo
            Map<String, Object> output = new HashMap<>();
            output.put("deliveryDate", estimatedDate.toString());

            client.newCompleteCommand(job.getKey())
                    .variables(output)
                    .send()
                    .join();

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao calcular data: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
