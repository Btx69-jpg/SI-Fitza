package com.camunda.handles;

import com.camunda.classes.RegistoLote.CleaningLine;
import com.camunda.classes.RegistoLote.Cliente;
import com.camunda.classes.RegistoLote.Enums.TypePizza;
import com.camunda.classes.RegistoLote.Lote;
import com.camunda.classes.RegistoLote.RawMaterialUsed;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.HashMap;
import java.util.Map;

public class CreateLoteHandle implements JobHandler {
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println(">>> WORKER INICIADO: A criar Lote...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            String loteId = (String) variables.get("loteId");
            String typePizzaStr = (String) variables.get("typePizza");
            TypePizza typePizza = null;

            if (typePizzaStr != null) {
                try {
                    typePizza = TypePizza.valueOf(typePizzaStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("Tipo inválido: " + typePizzaStr);
                }
            }
            Number qtyInput = (Number) variables.get("producedQuantity");
            float producedQuantity = (qtyInput != null) ? qtyInput.floatValue() : 0.0f;

            Boolean isOrder = (Boolean) variables.get("isOrder");
            Cliente clienteObj = null;

            if (isOrder) {
                String cId = (String) variables.get("clienteId");
                String cName = (String) variables.get("clienteName");

                if (cId != null && !cId.isEmpty() && cName != null) {
                    clienteObj = new Cliente(cId, cName);
                    System.out.println("Cliente associado: " + cName);
                }
            }

            Lote novoLote = new Lote(
                    loteId,
                    typePizza,
                    isOrder,
                    producedQuantity,
                    clienteObj,
                    new RawMaterialUsed[0],
                    new CleaningLine[0]
            );

            Map<String, Object> outputVariables = new HashMap<>();
            outputVariables.put("lote", novoLote);

            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();

            System.out.println(">>> SUCESSO: Lote criado com Cliente: " + (clienteObj != null ? "SIM" : "NÃO"));
        } catch (Exception e) {
            System.err.println("Erro ao criar lote: " + e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Falha ao converter ou criar Lote: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
