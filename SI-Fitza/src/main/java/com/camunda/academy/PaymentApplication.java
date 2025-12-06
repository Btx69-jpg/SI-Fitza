package com.camunda.academy;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.camunda.zeebe.client.api.response.Topology;

public class PaymentApplication {

    private static String CAMUNDA_AUTHORIZATION_SERVER_URL = "https://login.cloud.camunda.io/oauth/token";
    private static String CAMUNDA_TOKEN_AUDIENCE = "zeebe.camunda.io";
    private static String CAMUNDA_REST_ADDRESS = "https://bru-2.zeebe.camunda.io/dfdb8d36-5bf6-4b20-be42-8205ce0805f0";
    private static String CAMUNDA_GRPC_ADDRESS = "https://dfdb8d36-5bf6-4b20-be42-8205ce0805f0.bru-2.zeebe.camunda.io:443";
    private static String CAMUNDA_CLIENT_ID = "zbk3brnZhoUZZAcTKONeV2o_55vZmy2s";
    private static String CAMUNDA_CLIENT_SECRET = "aWg6XnQV0r7VsUGJP5cu7IMjaV0WFx5mCvbaWrS1z5dVNJfLWeXDST5aTVb93qea";

    public static void main(String[] args) {
        final OAuthCredentialsProvider credentialsProvider = new OAuthCredentialsProviderBuilder()
                .authorizationServerUrl(CAMUNDA_AUTHORIZATION_SERVER_URL)
                .audience(CAMUNDA_TOKEN_AUDIENCE)
                .clientId(CAMUNDA_CLIENT_ID)
                .clientSecret(CAMUNDA_CLIENT_SECRET)
                .build();

        // 2. Criar o Cliente Zeebe (A conexão real)
        try (final ZeebeClient client = ZeebeClient.newClientBuilder()
                .grpcAddress(CAMUNDA_GRPC_ADDRESS)
                .restAddress(CAMUNDA_REST_ADDRESS)
                .credentialsProvider(credentialsProvider)
                .build()) {

            System.out.println("A conectar ao Camunda 8...");

            // 3. Testar a conexão pedindo a Topologia do cluster
            final Topology topology = client.newTopologyRequest().send().join();

            System.out.println("Conexão com sucesso!");
            System.out.println("Cluster topology: " + topology);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
