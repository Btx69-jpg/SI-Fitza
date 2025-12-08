package com.camunda.handles.ProgramarProducao;

import com.camunda.classes.ProgramarProducao.Machine;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.*;

/**
 * {@code CheckMachineStatusHandle} é um {@link io.camunda.zeebe.client.api.worker.JobHandler}
 * responsável por **simular a verificação do estado operacional** dos equipamentos
 * (máquinas) necessários para a produção.
 *
 * <p>Esta classe simula um diagnóstico de linha, determinando aleatoriamente
 * quantas e quais máquinas estão avariadas, e envia o resultado ({@code isEquipmentReady})
 * de volta ao processo Camunda.
 *
 * <p>Se forem detetadas falhas, envia detalhes adicionais ({@code machineId} e {@code errorDetails})
 * para o tratamento de exceção subsequente.
 */
public class CheckMachineStatusHandle implements JobHandler {

    /**
     * Trata a tarefa (Job) ativada do Camunda Zeebe.
     *
     * <p>A lógica de simulação é a seguinte:
     * <ol>
     * <li>Cria uma lista *mock* de máquinas de produção.</li>
     * <li>Gera um número aleatório de 0 a N (número total de máquinas) para simular avarias.</li>
     * <li>Marca aleatoriamente as máquinas escolhidas como avariadas.</li>
     * <li>Gera o {@code isEquipmentReady} (booleano) e o {@code errorDetails} (relatório).</li>
     * <li>Completa a tarefa, encaminhando para o passo seguinte (se estiver operacional)
     * ou para o caminho de manutenção (se houver falhas).</li>
     * </ol>
     *
     * @param client O cliente do Job para enviar comandos de conclusão ({@code complete}) ou falha ({@code fail}).
     * @param job O Job ativado que contém os detalhes da tarefa.
     * @throws Exception Se ocorrer um erro interno, embora o erro seja tratado
     * com o comando {@code fail}.
     */
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

            //Random 1: Definir QUANTAS máquinas estão avariadas (0 a 5)
            int numberOfFailures = random.nextInt(machines.size() + 1);

            System.out.println("   > Simulação: " + numberOfFailures + " máquinas com avaria detetada.");

            //Random 2: Escolher QUAIS máquinas avariam
            Collections.shuffle(machines);

            for (int i = 0; i < numberOfFailures; i++) {
                machines.get(i).setBroken("ERRO CRÍTICO: Falha no motor/sensor");
            }

            //Verificar estado global e gerar relatório
            boolean isEquipmentReady = true;
            StringBuilder report = new StringBuilder("--- Relatório de Equipamentos ---\n");

            for (Machine m : machines) {
                report.append(m.toString()).append("\n");

                if (!m.isWorking()) {
                    isEquipmentReady = false;
                    System.out.println("   [X] FALHA: " + m.getName());
                } else {
                    System.out.println("   [V] OK: " + m.getName());
                }
            }

            //Enviar decisão para o Camunda
            Map<String, Object> output = new HashMap<>();
            output.put("isEquipmentReady", isEquipmentReady);

            if (!isEquipmentReady) {
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
