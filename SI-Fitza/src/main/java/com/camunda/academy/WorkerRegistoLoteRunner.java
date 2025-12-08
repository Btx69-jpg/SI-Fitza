package com.camunda.academy;

import com.camunda.handles.RegistoLote.*;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.Topology;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.util.Scanner;

public class WorkerRegistoLoteRunner {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static String CAMUNDA_AUTHORIZATION_SERVER_URL = dotenv.get("CAMUNDA_AUTHORIZATION_SERVER_URL");
    private static String CAMUNDA_TOKEN_AUDIENCE = dotenv.get("CAMUNDA_TOKEN_AUDIENCE");
    private static String CAMUNDA_REST_ADDRESS = dotenv.get("CAMUNDA_REST_ADDRESS");
    private static String CAMUNDA_GRPC_ADDRESS = dotenv.get("CAMUNDA_GRPC_ADDRESS");
    private static String CAMUNDA_CLIENT_ID = dotenv.get("CAMUNDA_CLIENT_ID");
    private static String CAMUNDA_CLIENT_SECRET = dotenv.get("CAMUNDA_CLIENT_SECRET");

    public static void main(String[] args) {
        if (CAMUNDA_CLIENT_ID == null || CAMUNDA_GRPC_ADDRESS == null) {
            System.err.println("ERRO: Variáveis de ambiente não encontradas. Verifica o ficheiro .env");
            return;
        }

        final OAuthCredentialsProvider credentialsProvider = new OAuthCredentialsProviderBuilder()
                .authorizationServerUrl(CAMUNDA_AUTHORIZATION_SERVER_URL)
                .audience(CAMUNDA_TOKEN_AUDIENCE)
                .clientId(CAMUNDA_CLIENT_ID)
                .clientSecret(CAMUNDA_CLIENT_SECRET)
                .build();

        String grpcAddressClean = CAMUNDA_GRPC_ADDRESS.replace("https://", "").replace(":443", "");
        String finalGrpcUri = "https://" + grpcAddressClean + ":443";

        System.out.println("A conectar ao Camunda 8...");

        // 4. Criação do Cliente (dentro de try-with-resources para fechar corretamente ao sair)
        try (final ZeebeClient client = ZeebeClient.newClientBuilder()
                .grpcAddress(URI.create(finalGrpcUri))
                .restAddress(URI.create(CAMUNDA_REST_ADDRESS))
                .credentialsProvider(credentialsProvider)
                .build()) {

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
                    .jobType("sendLabEmail") // Define este nome no teu Send Task no BPMN
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