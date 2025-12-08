package com.camunda.handles.RegistoLote;

import com.camunda.classes.RegistoLote.DiscartReason;
import com.camunda.classes.RegistoLote.Enums.ActorDiscartLote;
import com.camunda.classes.RegistoLote.Enums.LoteState;
import com.camunda.classes.RegistoLote.Lote;
import com.camunda.classes.RegistoLote.StateLote;
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

            String reasonMsg = (String) variables.getOrDefault("rejectionReason", "Motivo não especificado");

            Lote lote = LoteUtils.getLoteFromJob(job);
            DiscartReason motivoObjeto = new DiscartReason(reasonMsg, ActorDiscartLote.LABORATORY);
            StateLote novoEstado = new StateLote(motivoObjeto, LoteState.DISCARDED);

            lote.setLoteState(novoEstado);
            LoteUtils.saveLoteToDisk(lote);

            System.out.println("WARN: O Lote " + lote.getLoteId() + " foi descartado pelo Laboratório.");

            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey()).retries(0).errorMessage(e.getMessage()).send().join();
        }
    }
}
