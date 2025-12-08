package com.camunda.handles.RegistoLote;

import com.camunda.classes.RegistoLote.RawMaterial;
import com.camunda.classes.RegistoLote.RawMaterialUsed;
import com.camunda.classes.RegistoLote.Supplier;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import java.time.LocalDate;
import java.util.Map;

public class UpdateMaterialLoteHandle implements JobHandler {

    @Override
    public void handle(final JobClient client, final ActivatedJob job) {
        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            //Ler as Variaveis todas
            String rawMaterialId = (String) variables.get("rawMaterialId");
            String materialName = (String) variables.get("materialName");
            Double quantity = (Double) variables.get("quantity");
            String expirationDateString = (String) variables.get("expirationDate");
            LocalDate expirationDate = LocalDate.parse(expirationDateString);
            String supplierId = (String) variables.get("supplierId");
            String supplierName = (String) variables.get("supplierName");

            Supplier supplier = new Supplier(supplierId, supplierName);
            RawMaterial rawMaterial = new RawMaterial(rawMaterialId, materialName, supplier);
            RawMaterialUsed rawMaterialUsed = new RawMaterialUsed(rawMaterial, expirationDate, quantity);

            System.out.println(">>> Objeto 'RawMaterialUsed' criado com sucesso.");
            System.out.println(">>> Supplier: " + rawMaterialUsed.getRawMaterial().getSupplier().getName());
            System.out.println(">>> Qtd: " + rawMaterialUsed.getQuantity());

            // 4. Completar a tarefa
            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();
        } catch (Exception e) {
            // Se der erro, avisar o Zeebe para tentar de novo ou falhar
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao processar lote: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
