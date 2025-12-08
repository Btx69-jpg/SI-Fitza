package com.camunda.utils;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;

public class CamundaClientFactory {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static ZeebeClient createClient() {
        // 1. Ler Variáveis
        String authServerUrl = dotenv.get("CAMUNDA_AUTHORIZATION_SERVER_URL");
        String tokenAudience = dotenv.get("CAMUNDA_TOKEN_AUDIENCE");
        String restAddress = dotenv.get("CAMUNDA_REST_ADDRESS");
        String grpcAddress = dotenv.get("CAMUNDA_GRPC_ADDRESS");
        String clientId = dotenv.get("CAMUNDA_CLIENT_ID");
        String clientSecret = dotenv.get("CAMUNDA_CLIENT_SECRET");

        // Validação básica
        if (clientId == null || grpcAddress == null) {
            throw new RuntimeException("ERRO CRÍTICO: Variáveis de ambiente do Camunda não encontradas no .env");
        }

        // 2. Configurar Credenciais OAuth
        final OAuthCredentialsProvider credentialsProvider = new OAuthCredentialsProviderBuilder()
                .authorizationServerUrl(authServerUrl)
                .audience(tokenAudience)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();

        // 3. Limpar endereço gRPC (remover https:// e porta se necessário)
        String grpcAddressClean = grpcAddress.replace("https://", "").replace(":443", "");
        String finalGrpcUri = "https://" + grpcAddressClean + ":443";

        System.out.println(">>> A criar conexão ao Camunda 8...");

        // 4. Construir e devolver o cliente
        return ZeebeClient.newClientBuilder()
                .grpcAddress(URI.create(finalGrpcUri))
                .restAddress(URI.create(restAddress))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
