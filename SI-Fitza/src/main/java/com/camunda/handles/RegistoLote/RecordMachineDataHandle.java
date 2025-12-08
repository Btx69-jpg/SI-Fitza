package com.camunda.handles.RegistoLote;

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

/**
 * Worker do Camunda/Zeebe que simula a recolha de dados de equipamentos (MES/SCADA).
 * <p>
 * O objetivo principal é criar instâncias de subclasses de {@link Machine} (como {@link MixerMachine} e {@link OvenMachine}),
 * preenchê-las com dados simulados (incluindo valores aleatórios para simulação de sensores)
 * e, em seguida, serializar esta lista de objetos para o motor de processo Zeebe.
 * </p>
 * <p>
 * A lista de máquinas é serializada para a variável de processo **{@code temp_machines_list}**,
 * que será posteriormente consolidada no objeto {@code Lote} por outro Worker (`UpdateLoteProductionDataHandle`).
 * </p>
 *
 * <h3>Nota de Polimorfismo e Serialização (Jackson):</h3>
 * A classe inclui uma verificação explícita (`if (!jsonString.contains("machineType"))`)
 * para garantir que o Jackson está a serializar corretamente o polimorfismo,
 * exigindo a anotação {@code @JsonTypeInfo} na classe base {@link Machine}.
 */
public class RecordMachineDataHandle implements JobHandler {

    private final Random random = new Random();

    /**
     * Lógica de tratamento para o Job. Simula a leitura dos dados das máquinas,
     * serializa-os e envia-os para o contexto do processo.
     *
     * @param client O cliente do Job, usado para completar ou falhar o Job.
     * @param job O Job ativado pelo motor Zeebe.
     */
    @Override
    public void handle(final JobClient client, final ActivatedJob job) {
        System.out.println("\n>>> [MES SYSTEM] A iniciar recolha de dados das máquinas...");

        try {
            //Simulação de Leitura para MixerMachine
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

            //Simulação de Leitura para OvenMachine
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

            //Recolher todas as instâncias numa lista de polimorfismo
            List<Machine> collectedMachines = new ArrayList<>();
            collectedMachines.add(mixer);
            collectedMachines.add(oven);

            System.out.println(">>> [MES] 2 Máquinas registadas. A enviar para merge.");

            //Serializa para String primeiro para realizar a verificação do tipo
            String jsonString = LoteUtils.getMapper()
                    .writerFor(new TypeReference<List<Machine>>() {})
                    .writeValueAsString(collectedMachines);

            if (!jsonString.contains("machineType")) {
                throw new RuntimeException("ERRO CRÍTICO: O JSON gerado não tem o campo 'machineType'. Verifica as anotações na classe Machine!");
            }

            //Converte o JSON de volta para List<Map<String, Object>>
            List<Map<String, Object>> serializedMachines = LoteUtils.getMapper().readValue(
                    jsonString,
                    new TypeReference<>() {}
            );

            //Enviar para o processo (variável temporária)
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
