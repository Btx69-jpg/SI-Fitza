package com.camunda.handles.ProgramarProducao;

import com.camunda.classes.ProgramarProducao.Machine;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.*;

public class CheckMachineStatusHandle implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: VERIFICAR EQUIPAMENTOS] A iniciar diagnóstico da linha...");

        try {

            List<Machine> machines = new ArrayList<>();
            machines.add(new Machine("M-01", "Misturadora Principal"));
            machines.add(new Machine("M-02", "Misturadora Secundária"));
            machines.add(new Machine("F-01", "Forno de Túnel A"));
            machines.add(new Machine("F-02", "Forno de Túnel B"));
            machines.add(new Machine("E-01", "Embaladora Automática"));

            Random random = new Random();

            // 2. Random 1: Definir QUANTAS máquinas estão avariadas (0 a 5)
            int numberOfFailures = random.nextInt(machines.size() + 1);

            // Para testes: Se quiseres forçar 0 avarias a maioria das vezes:
            // if (random.nextDouble() > 0.3) numberOfFailures = 0;

            System.out.println("   > Simulação: " + numberOfFailures + " máquinas com avaria detetada.");

            // 3. Random 2: Escolher QUAIS máquinas avariam
            // A forma mais fácil é baralhar a lista e pegar nas primeiras N
            Collections.shuffle(machines);

            for (int i = 0; i < numberOfFailures; i++) {
                // Marca as primeiras 'numberOfFailures' da lista baralhada como avariadas
                machines.get(i).setBroken("ERRO CRÍTICO: Falha no motor/sensor");
            }

            // 4. Verificar estado global e gerar relatório
            boolean isEquipmentReady = true;
            StringBuilder report = new StringBuilder("--- Relatório de Equipamentos ---\n");

            for (Machine m : machines) { // Iterar a lista (que está baralhada, mas não faz mal)
                report.append(m.toString()).append("\n");

                if (!m.isWorking()) {
                    isEquipmentReady = false;
                    System.out.println("   [X] FALHA: " + m.getName());
                } else {
                    System.out.println("   [V] OK: " + m.getName());
                }
            }

            // 5. Enviar decisão para o Camunda
            Map<String, Object> output = new HashMap<>();
            output.put("isEquipmentReady", isEquipmentReady);

            if (!isEquipmentReady) {
                // Se houver falhas, passamos o nome da primeira máquina avariada (ou todas) para o email
                output.put("machineId", "Linha de Produção (Várias)");
                output.put("errorDetails", report.toString());
                System.out.println(">>> RESULTADO: Manutenção necessária.");
            } else {
                System.out.println(">>> RESULTADO: Linha 100% Operacional. A avançar.");
            }

            client.newCompleteCommand(job.getKey())
                    .variables(output)
                    .send()
                    .join();

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro na verificação: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
