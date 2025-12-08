package com.camunda.academy;

import com.camunda.handles.RegistoLote.*;
import com.camunda.utils.CamundaClientFactory;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.Topology;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.util.Scanner;

public class WorkerRegistoLoteRunner {
    public static void main(String[] args) {
        try (final ZeebeClient client = CamundaClientFactory.createClient()) {

            final Topology topology = client.newTopologyRequest().send().join();
            System.out.println("Conexão com sucesso! Cluster size: " + topology.getClusterSize());

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

            System.out.println("\n [SISTEMA A CORRER] O worker está à espera de tarefas.");
            System.out.println(" Prime ENTER para parar a aplicação e sair...");

            Scanner sc = new Scanner(System.in);
            sc.nextLine();

            System.out.println("A encerrar o worker...");
        } catch (Exception e) {
            System.err.println("Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
