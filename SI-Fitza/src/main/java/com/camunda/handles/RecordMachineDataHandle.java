package com.camunda.handles;

import com.camunda.classes.RegistoLote.Enums.MachineStatus;
import com.camunda.classes.RegistoLote.machines.Machine;
import com.camunda.classes.RegistoLote.machines.MixerMachine;
import com.camunda.classes.RegistoLote.machines.OvenMachine;
import com.fasterxml.jackson.core.type.TypeReference;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import com.camunda.utils.LoteUtils;

import java.util.*;

public class RecordMachineDataHandle implements JobHandler {

    private final Random random = new Random();

    @Override
    public void handle(final JobClient client, final ActivatedJob job) {
        System.out.println("\n>>> [MES SYSTEM] A iniciar recolha de dados das máquinas...");
        try {
            double rpm = 110 + (random.nextDouble() * 15);
            double doughTemp = 23 + (random.nextDouble() * 3);

            MixerMachine mixer = new MixerMachine(
                    "MIX-01",
                    "Misturadora Principal",
                    MachineStatus.STOPPED,
                    rpm,
                    doughTemp,
                    14.5
            );

            double t1 = 280 + (random.nextDouble() * 5);
            double t2 = 300 + (random.nextDouble() * 5);

            OvenMachine oven = new OvenMachine(
                    "OVN-Tunnel-A",
                    "Forno de Cozedura",
                    MachineStatus.STOPPED,
                    t1,
                    t2,
                    45.0
            );

            List<Machine> collectedMachines = new ArrayList<>();
            collectedMachines.add(mixer);
            collectedMachines.add(oven);

            System.out.println(">>> [MES] 2 Máquinas registadas. A enviar para merge.");

            boolean temAnotacao = Machine.class.isAnnotationPresent(com.fasterxml.jackson.annotation.JsonTypeInfo.class);
            System.out.println(">>> DEBUG: A classe Machine tem anotação @JsonTypeInfo? " + temAnotacao);

            String jsonString = LoteUtils.getMapper()
                    .writerFor(new TypeReference<List<Machine>>() {})
                    .writeValueAsString(collectedMachines);

            if (!jsonString.contains("machineType")) {
                throw new RuntimeException("ERRO CRÍTICO: O JSON gerado não tem o campo 'machineType'. Verifica as anotações na classe Machine!");
            }

            List<Map<String, Object>> serializedMachines = LoteUtils.getMapper().readValue(
                    jsonString,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            Map<String, Object> outputVariables = new HashMap<>();
            outputVariables.put("temp_machines_list", serializedMachines);

            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao registar dados das máquinas: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
