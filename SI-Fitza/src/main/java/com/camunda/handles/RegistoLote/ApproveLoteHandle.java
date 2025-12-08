package com.camunda.handles.RegistoLote;

import com.camunda.classes.RegistoLote.Enums.LoteState;
import com.camunda.classes.RegistoLote.Lote;
import com.camunda.classes.RegistoLote.StateLote;
import com.camunda.utils.LoteUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

/**
 * Worker do Camunda/Zeebe responsável por **aprovar e finalizar um {@link Lote}**
 * para distribuição ou uso.
 * <p>
 * Este Worker é ativado após a conclusão de todas as etapas de produção e análise
 * (incluindo a aprovação do laboratório), marcando o Lote como aceitável.
 * </p>
 *
 * <h3>Operações:</h3>
 * <ol>
 * <li>Recupera o objeto {@link Lote} do contexto do processo.</li>
 * <li>Cria um novo estado, definindo o {@link LoteState} como **APROVED** (Aprovado).</li>
 * <li>Atualiza o estado do {@code Lote} (sem motivo de descarte, pois foi aprovado).</li>
 * <li>Guarda o estado final do {@code Lote} em disco (backup/auditoria) usando {@link LoteUtils#saveLoteToDisk(Lote)}.</li>
 * <li>Completa o Job, sinalizando o fim do ciclo de vida principal do Lote no processo.</li>
 * </ol>
 */
public class ApproveLoteHandle implements JobHandler {

    /**
     * Lógica de tratamento para o Job. Processa a aprovação, atualiza o estado do Lote
     * para {@code APROVED} e completa a tarefa.
     *
     * @param client O cliente do Job, usado para completar ou falhar o Job.
     * @param job O Job ativado pelo motor Zeebe.
     * @throws Exception Se ocorrer um erro durante a deserialização do Lote ou gravação em disco.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        try {
            System.out.println("\n>>> [FINALIZAR] A desbloquear lote aprovado...");

            //Obter o Lote principal do contexto e atualizar o estado
            Lote lote = LoteUtils.getLoteFromJob(job);
            StateLote novoEstado = new StateLote(null, LoteState.APROVED);

            lote.setLoteState(novoEstado);
            LoteUtils.saveLoteToDisk(lote);

            //Completar o Job
            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

            System.out.println(">>> SUCESSO: Lote " + lote.getLoteId() + " aprovado.");
        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey()).retries(0).errorMessage(e.getMessage()).send().join();
        }
    }
}