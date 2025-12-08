package com.camunda.handles.RegistoLote;

import com.camunda.classes.RegistoLote.Lote;
import com.camunda.classes.RegistoLote.machines.Machine;
import com.camunda.classes.RegistoLote.sensores.RoomSensor;
import com.camunda.utils.LoteUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.List;
import java.util.Map;

/**
 * Worker do Camunda/Zeebe projetado para **consolidar e fundir dados paralelos**
 * no objeto {@link Lote} principal.
 * <p>
 * Este Worker é tipicamente executado após um *Gateway de Convergência* (`Merge Gateway`)
 * em um fluxo de processo, onde vários caminhos paralelos geraram dados temporários (ex: leituras de sensores, estado das máquinas)
 * que precisam ser anexados ao objeto de domínio central (`Lote`).
 * </p>
 *
 * <h3>Variáveis de Processo Tratadas (Temporárias):</h3>
 * <ul>
 * <li>{@code temp_machines_list}: Lista de leituras de {@link Machine} a serem adicionadas ao Lote.</li>
 * <li>{@code temp_sensors_list}: Lista de leituras de {@link RoomSensor} a serem adicionadas ao Lote.</li>
 * </ul>
 * <p>
 * O método garante a atualização do {@code "lote"} e **limpa** as variáveis temporárias após a consolidação.
 * </p>
 */
public class UpdateLoteProductionDataHandle implements JobHandler {

    /**
     * Lógica de tratamento para o Job, responsável por fundir os dados temporários
     * no objeto Lote principal.
     *
     * @param client O cliente do Job, usado para completar ou falhar o Job.
     * @param job O Job ativado pelo motor Zeebe.
     * @throws Exception Se ocorrer um erro durante a deserialização ou processamento.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [MERGE TASK] A consolidar dados paralelos no Lote...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            //Obter o objeto Lote principal do contexto
            Lote loteMain = LoteUtils.getLoteFromJob(job);

            //Consolidar leituras de Máquinas
            if (variables.containsKey("temp_machines_list")) {
                List<Machine> newMachines = LoteUtils.getMapper().convertValue(
                        variables.get("temp_machines_list"),
                        new TypeReference<>() {}
                );

                if (newMachines != null) {
                    for (Machine m : newMachines) loteMain.addMachineReading(m);
                    System.out.println("   + " + newMachines.size() + " máquinas adicionadas.");
                }
            }

            //Consolidar leituras de Sensores de Ambiente
            if (variables.containsKey("temp_sensors_list")) {
                List<RoomSensor> newSensors = LoteUtils.getMapper().convertValue(
                        variables.get("temp_sensors_list"),
                        new TypeReference<>() {}
                );

                if (newSensors != null) {
                    for (RoomSensor s : newSensors) loteMain.addSensorRoomReading(s);
                    System.out.println("   + " + newSensors.size() + " sensores adicionados.");
                }
            }

            // 4. Preparar saída: Envolver o Lote atualizado
            Map<String, Object> output = LoteUtils.wrapLoteVariable(loteMain);

            //Limpar variáveis temporárias após a consolidação
            output.put("temp_sensors_list", null);
            output.put("temp_machines_list", null);

            //Completa o Job com as variáveis atualizadas
            client.newCompleteCommand(job.getKey())
                    .variables(output)
                    .send()
                    .join();

            System.out.println(">>> [MERGE] Lote consolidado e salvo com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro no Merge: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
