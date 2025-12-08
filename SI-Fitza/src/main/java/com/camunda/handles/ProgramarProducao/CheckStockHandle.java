package com.camunda.handles.ProgramarProducao;

import com.camunda.classes.ProgramarProducao.MaterialNeeded;
import com.camunda.utils.LoteUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CheckStockHandle implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> A verificar disponibilidade de stock...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            // Recuperar a lista calculada na tarefa anterior
            List<MaterialNeeded> requiredList = LoteUtils.getMapper().convertValue(
                    variables.get("materialsNeededList"),
                    new TypeReference<List<MaterialNeeded>>() {}
            );

            boolean isStockAvailable = true;
            StringBuilder missingItems = new StringBuilder();
            Random random = new Random();


            for (MaterialNeeded item : requiredList) {
                // Simula consulta do stock
                boolean itemInStock = random.nextDouble() > 0.2;

                if (!itemInStock) {
                    isStockAvailable = false;
                    missingItems.append(item.getRawMaterial().getName())
                            .append(" (Qtd: ").append(item.getQuantity()).append("); ");
                    System.out.println("    EM FALTA: " + item.getRawMaterial().getName());
                } else {
                    System.out.println("   Disponível: " + item.getRawMaterial().getName());
                }
            }

            Map<String, Object> output = new HashMap<>();
            output.put("isStockAvailable", isStockAvailable);

            if (!isStockAvailable) {
                // Prepara mensagem para o email do fornecedor
                output.put("missingMaterialsReport", missingItems.toString());
            }

            client.newCompleteCommand(job.getKey())
                    .variables(output)
                    .send()
                    .join();

            System.out.println(">>>  Verificação concluída. Stock disponível? " + isStockAvailable);

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey()).retries(0).errorMessage(e.getMessage()).send().join();
        }
    }
}
