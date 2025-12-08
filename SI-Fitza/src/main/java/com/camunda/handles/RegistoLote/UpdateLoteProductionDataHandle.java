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

//Meter para também, se calhar atualizar os dados do formulario de limpeza
public class UpdateLoteProductionDataHandle implements JobHandler {
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [MERGE TASK] A consolidar dados paralelos no Lote...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            Lote loteMain = LoteUtils.getLoteFromJob(job);

            if (variables.containsKey("temp_machines_list")) {
                List<Machine> newMachines = LoteUtils.getMapper().convertValue(
                        variables.get("temp_machines_list"),
                        new TypeReference<List<Machine>>() {}
                );

                if (newMachines != null) {
                    for (Machine m : newMachines) loteMain.addMachineReading(m);
                    System.out.println("   + " + newMachines.size() + " máquinas adicionadas.");
                }
            }


            if (variables.containsKey("temp_sensors_list")) {
                List<RoomSensor> newSensors = LoteUtils.getMapper().convertValue(
                        variables.get("temp_sensors_list"),
                        new TypeReference<List<RoomSensor>>() {}
                );

                if (newSensors != null) {
                    for (RoomSensor s : newSensors) loteMain.addSensorRoomReading(s);
                    System.out.println("   + " + newSensors.size() + " sensores adicionados.");
                }
            }

            // 4. Preparar saída
            Map<String, Object> output = LoteUtils.wrapLoteVariable(loteMain);

            output.put("temp_sensors_list", null);
            output.put("temp_machines_list", null);

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
