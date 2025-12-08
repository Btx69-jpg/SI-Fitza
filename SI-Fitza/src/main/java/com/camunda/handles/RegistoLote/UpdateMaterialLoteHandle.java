package com.camunda.handles.RegistoLote;

import com.camunda.classes.RawMaterial;
import com.camunda.classes.RawMaterialUsed;
import com.camunda.classes.RegistoLote.Lote;
import com.camunda.classes.Supplier;
import com.camunda.utils.LoteUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class UpdateMaterialLoteHandle implements JobHandler {

    @Override
    public void handle(final JobClient client, final ActivatedJob job) {
        try {
            System.out.println(">>> [MATERIAL] A processar lista de materiais...");

            Map<String, Object> variables = job.getVariablesAsMap();

            // 1. Obter o Lote Atual do processo
            Lote lote = LoteUtils.getLoteFromJob(job);

            // 2. Verificar se a lista existe (O nome tem de bater certo com o JSON: "listMaterials")
            if (variables.containsKey("listMaterials")) {
                List<Map<String, Object>> rawList = LoteUtils.getMapper().convertValue(
                        variables.get("listMaterials"),
                        new TypeReference<>() {}
                );

                if (rawList != null) {
                    // 3. Iterar sobre cada material da lista
                    for (Map<String, Object> item : rawList) {

                        String rId = (String) item.getOrDefault("rawMaterialId", "N/A");
                        String rName = (String) item.getOrDefault("materialName", "Desconhecido");

                        Number qtyNum = (Number) item.get("quantity");
                        double quantity = (qtyNum != null) ? qtyNum.doubleValue() : 0.0;

                        String expDateStr = (String) item.get("expirationDate");
                        LocalDate expirationDate;
                        if (expDateStr != null && !expDateStr.isEmpty()) {
                            expirationDate = LocalDate.parse(expDateStr);
                        } else {
                            expirationDate = LocalDate.now().plusYears(1); // Default se vier null
                        }

                        String sId = (String) item.getOrDefault("supplierId", "N/A");
                        String sName = (String) item.getOrDefault("supplierName", "Desconhecido");

                        Supplier supplier = new Supplier(sId, sName);
                        RawMaterial rawMaterial = new RawMaterial(rId, rName, supplier);
                        RawMaterialUsed rawMaterialUsed = new RawMaterialUsed(rawMaterial, expirationDate, quantity);

                        lote.getRawMaterialUsed().add(rawMaterialUsed);
                        System.out.println("   + Material Adicionado: " + rName + " | Qtd: " + quantity);
                    }
                }
            } else {
                System.out.println(">>> AVISO: Variável 'listMaterials' não encontrada.");
            }

            Map<String, Object> outputVariables = LoteUtils.wrapLoteVariable(lote);

            outputVariables.put("listMaterials", null);
            outputVariables.put("rawMaterialId", null);
            outputVariables.put("materialName", null);

            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();

            System.out.println(">>> SUCESSO: Materiais atualizados no Lote " + lote.getLoteId());

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao processar lista de materiais: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
