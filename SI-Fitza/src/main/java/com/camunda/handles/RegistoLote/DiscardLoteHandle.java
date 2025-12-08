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

/**
 * Worker do Camunda/Zeebe responsável por **marcar um {@link Lote} como descartado/rejeitado**
 * e persistir esta decisão.
 * <p>
 * Este Worker é tipicamente ativado após uma decisão humana (neste caso, do Laboratório)
 * que determina que a qualidade do Lote é inaceitável.
 * </p>
 *
 * <h3>Operações:</h3>
 * <ol>
 * <li>Recupera o objeto {@link Lote} do contexto do processo.</li>
 * <li>Lê a variável de processo {@code "rejectionReason"} para obter a justificação do descarte.</li>
 * <li>Cria o objeto {@link DiscartReason} e define o {@link ActorDiscartLote} como **LABORATORY**.</li>
 * <li>Atualiza o estado do {@code Lote} para {@link LoteState#DISCARDED}.</li>
 * <li>Guarda o estado final do {@code Lote} em disco (backup/auditoria) usando {@link LoteUtils#saveLoteToDisk(Lote)}.</li>
 * </ol>
 */
public class DiscardLoteHandle implements JobHandler {

    /**
     * Lógica de tratamento para o Job. Processa a rejeição, atualiza o estado do Lote
     * e notifica o motor Zeebe.
     *
     * @param client O cliente do Job, usado para completar ou falhar o Job.
     * @param job O Job ativado pelo motor Zeebe.
     * @throws Exception Se ocorrer um erro durante a deserialização do Lote ou gravação em disco.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        try {
            System.out.println("\n>>> [FINALIZAR] A processar rejeição do lote...");

            Map<String, Object> variables = job.getVariablesAsMap();

            //Obter o motivo do descarte
            String reasonMsg = (String) variables.getOrDefault("rejectionReason", "Motivo não especificado");

            //Obter o Lote e criar novos objetos de estado
            Lote lote = LoteUtils.getLoteFromJob(job);
            DiscartReason motivoObjeto = new DiscartReason(reasonMsg, ActorDiscartLote.LABORATORY);
            StateLote novoEstado = new StateLote(motivoObjeto, LoteState.DISCARDED);

            //Atualizar o estado do Lote e persistir
            lote.setLoteState(novoEstado);
            LoteUtils.saveLoteToDisk(lote);

            System.out.println("WARN: O Lote " + lote.getLoteId() + " foi descartado pelo Laboratório.");

            //Completar o Job
            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey()).retries(0).errorMessage(e.getMessage()).send().join();
        }
    }
}
