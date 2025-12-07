package com.camunda.academy;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.camunda.zeebe.client.api.response.Topology;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;

public class PaymentApplication {
    private static final Dotenv dotenv = Dotenv.load();

    private static String CAMUNDA_AUTHORIZATION_SERVER_URL = dotenv.get("CAMUNDA_AUTHORIZATION_SERVER_URL");
    private static String CAMUNDA_TOKEN_AUDIENCE = dotenv.get("CAMUNDA_TOKEN_AUDIENCE");
    private static String CAMUNDA_REST_ADDRESS = dotenv.get("CAMUNDA_REST_ADDRESS");
    private static String CAMUNDA_GRPC_ADDRESS = dotenv.get("CAMUNDA_GRPC_ADDRESS");
    private static String CAMUNDA_CLIENT_ID = dotenv.get("CAMUNDA_CLIENT_ID");
    private static String CAMUNDA_CLIENT_SECRET = dotenv.get("CAMUNDA_CLIENT_SECRET");

    public static void main(String[] args) {
        final OAuthCredentialsProvider credentialsProvider = new OAuthCredentialsProviderBuilder()
                .authorizationServerUrl(CAMUNDA_AUTHORIZATION_SERVER_URL)
                .audience(CAMUNDA_TOKEN_AUDIENCE)
                .clientId(CAMUNDA_CLIENT_ID)
                .clientSecret(CAMUNDA_CLIENT_SECRET)
                .build();

        try (final ZeebeClient client = ZeebeClient.newClientBuilder()
                .grpcAddress(URI.create(CAMUNDA_GRPC_ADDRESS))
                .restAddress(URI.create(CAMUNDA_REST_ADDRESS))
                .credentialsProvider(credentialsProvider)
                .build()) {

            System.out.println("A conectar ao Camunda 8...");

            final Topology topology = client.newTopologyRequest().send().join();

            System.out.println("Conexão com sucesso!");
            System.out.println("Cluster topology: " + topology);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}