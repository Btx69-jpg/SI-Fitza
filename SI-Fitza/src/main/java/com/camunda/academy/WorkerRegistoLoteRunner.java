package com.camunda.academy;

import com.camunda.handles.RegistoLote.*;
import com.camunda.utils.CamundaClientFactory;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.Topology;

import java.util.Scanner;

/**
 * Classe principal (Main Runner) para inicializar e executar todos os **Workers**
 * responsáveis por manipular o ciclo de vida do Lote de Produção no Camunda/Zeebe.
 * <p>
 * Esta aplicação é responsável por:
 * <ul>
 * <li>Estabelecer a conexão com o Camunda Platform 8 (via {@link CamundaClientFactory}).</li>
 * <li>Registrar todos os `JobHandlers` (Workers) necessários para processar as tarefas de serviço externas definidas nos modelos BPMN.</li>
 * <li>Manter a aplicação em execução para que os Workers possam ouvir os Jobs.</li>
 * </ul>
 * </p>
 */
public class WorkerRegistoLoteRunner {

    /**
     * O ponto de entrada principal da aplicação.
     * <p>
     * 1. Cria o {@link ZeebeClient} usando a Factory.
     * 2. Verifica a conexão com a topologia do Cluster.
     * 3. Chama {@link #registerWorkers(ZeebeClient)} para ativar os manipuladores.
     * 4. Mantém a aplicação em estado de escuta até que o utilizador prima ENTER.
     * </p>
     *
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        // Uso de try-with-resources garante que o cliente Zeebe é fechado automaticamente
        try (final ZeebeClient client = CamundaClientFactory.createClient()) {
            // Teste de Conexão
            final Topology topology = client.newTopologyRequest().send().join();
            System.out.println("Conexão com sucesso! Cluster size: " + topology.getClusterSize());

            registerWorkers(client);

            System.out.println("\n [SISTEMA A CORRER] O worker está à espera de tarefas.");
            System.out.println(" Prime ENTER para parar a aplicação e sair...");

            // Bloqueia o thread principal para manter os workers abertos
            Scanner sc = new Scanner(System.in);
            sc.nextLine();

            System.out.println("A encerrar o worker...");
        } catch (Exception e) {
            System.err.println("Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Registra todos os manipuladores de Job (Workers) para os respetivos tipos de Job
     * definidos nos modelos BPMN.
     * <p>
     * O tempo limite padrão (`timeout`) define por quanto tempo um Worker retém um Job
     * se não for concluído.
     * </p>
     *
     * @param client A instância do {@link ZeebeClient} já inicializada e conectada.
     */
    private static void registerWorkers(ZeebeClient client) {
        client.newWorker()
                .jobType("createLote")
                .handler(new CreateLoteHandle())
                .name("createLoteWorker")
                .timeout(10000)
                .open();

        System.out.println(">>> JobWorker 'CreateLoteHandle' registado e ativo.");

        client.newWorker()
                .jobType("updateMaterialLote")
                .handler(new UpdateMaterialLoteHandle())
                .name("updateMaterialWorker")
                .timeout(10000)
                .open();

        System.out.println(">>> JobWorker 'UpdateMaterialLoteHandle' registado e ativo.");

        client.newWorker()
                .jobType("recordMachineData")
                .handler(new RecordMachineDataHandle())
                .name("mesWorker")
                .timeout(10000)
                .open();

        System.out.println(">>> JobWorker 'RecordMachineDataHandle' registado e ativo.");

        client.newWorker()
                .jobType("recordAmbientSensorData")
                .handler(new RecordAmbientSensorDataHandle())
                .name("IOTWorker")
                .timeout(10000)
                .open();

        System.out.println(">>> JobWorker 'RecordAmbientSensorDataHandle' registado e ativo.");

        client.newWorker()
                .jobType("updateLoteProductionData")
                .handler(new UpdateLoteProductionDataHandle())
                .name("mergeWorker")
                .open();

        System.out.println(">>> JobWorker 'UpdateLoteProductionDataHandle' registado e ativo.");

        client.newWorker()
                .jobType("sendLabEmail")
                .handler(new SendLabSampleEmailHandle())
                .name("sendLabEmailWorker")
                .open();

        System.out.println(">>> JobWorker 'SendLabSampleEmailHandle' registado e ativo.");

        client.newWorker()
                .jobType("discardLote")
                .handler(new DiscardLoteHandle())
                .name("discardLoteWorker")
                .open();

        System.out.println(">>> JobWorker 'DiscardLoteHandle' registado e ativo.");


        client.newWorker()
                .jobType("approveLote")
                .handler(new ApproveLoteHandle())
                .name("approveLoteWorker")
                .open();

        System.out.println(">>> JobWorker 'ApproveLoteHandle' registado e ativo.");
    }
}
