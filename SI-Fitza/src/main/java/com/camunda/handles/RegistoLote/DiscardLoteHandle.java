package com.camunda.handles.RegistoLote;

import com.camunda.classes.RegistoLote.Enums.LoteState;
import com.camunda.classes.RegistoLote.Lote;
import com.camunda.utils.LoteUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.Map;

public class DiscardLoteHandle implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        try {
            System.out.println("\n>>> [FINALIZAR] A processar rejeição do lote...");

            Map<String, Object> variables = job.getVariablesAsMap();

            String discardReason = (String) variables.getOrDefault("rejectionReason", "Motivo não especificado pelo Laboratório");

            Lote lote = LoteUtils.getLoteFromJob(job);
            lote.getLoteState().setState(LoteState.DISCARDED);
            lote.getLoteState().setDiscartReason(discardReason);

            LoteUtils.saveLoteToDisk(lote);

            System.out.println("WARN: O Lote " + lote.getLoteId() + " foi descartado.");

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey()).retries(0).errorMessage(e.getMessage()).send().join();
        }
    }
}
