package com.camunda.utils;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;

/**
 * Classe Factory responsável por criar e configurar instâncias do {@link ZeebeClient}
 * para interagir com o motor de orquestração Camunda Platform 8.
 * <p>
 * O cliente é configurado para usar a autenticação <b>OAuth 2.0 (Client Credentials)</b>
 * e os endereços de serviço (gRPC e REST) definidos em variáveis de ambiente.
 * </p>
 *
 * <h3>Requisitos do Ficheiro .env:</h3>
 * Esta classe depende das seguintes variáveis de ambiente essenciais para a conexão:
 * <ul>
 * <li>{@code CAMUNDA_GRPC_ADDRESS}: Endereço base para a comunicação gRPC (Broker).</li>
 * <li>{@code CAMUNDA_REST_ADDRESS}: Endereço base para a API REST.</li>
 * <li>{@code CAMUNDA_AUTHORIZATION_SERVER_URL}: URL do servidor de autorização (ex: Keycloak, Auth0).</li>
 * <li>{@code CAMUNDA_TOKEN_AUDIENCE}: O recurso (audience) para o qual o token deve ser emitido.</li>
 * <li>{@code CAMUNDA_CLIENT_ID}: ID do cliente OAuth.</li>
 * <li>{@code CAMUNDA_CLIENT_SECRET}: Segredo do cliente OAuth.</li>
 * </ul>
 */
public class CamundaClientFactory {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    /**
     * Cria, configura e devolve uma nova instância do {@link ZeebeClient}.
     * <p>
     * O cliente é configurado para usar a autenticação OAuth (Client Credentials)
     * e liga-se aos endpoints gRPC e REST definidos nas variáveis de ambiente.
     * </p>
     * <p>
     * <b>Nota:</b> O método realiza uma limpeza no {@code CAMUNDA_GRPC_ADDRESS} para garantir
     * que a porta 443 é anexada corretamente se estiver ausente, seguindo o padrão
     * de conexão do Camunda 8.
     * </p>
     *
     * @return Uma instância configurada do {@link ZeebeClient} pronta a ser usada.
     * @throws RuntimeException Se as variáveis de ambiente essenciais ({@code CAMUNDA_CLIENT_ID}
     * ou {@code CAMUNDA_GRPC_ADDRESS}) não forem encontradas ou forem nulas.
     */
    public static ZeebeClient createClient() {
        //Ler Variáveis
        String authServerUrl = dotenv.get("CAMUNDA_AUTHORIZATION_SERVER_URL");
        String tokenAudience = dotenv.get("CAMUNDA_TOKEN_AUDIENCE");
        String restAddress = dotenv.get("CAMUNDA_REST_ADDRESS");
        String grpcAddress = dotenv.get("CAMUNDA_GRPC_ADDRESS");
        String clientId = dotenv.get("CAMUNDA_CLIENT_ID");
        String clientSecret = dotenv.get("CAMUNDA_CLIENT_SECRET");

        //Validação básica
        if (clientId == null || grpcAddress == null) {
            throw new RuntimeException("ERRO CRÍTICO: Variáveis de ambiente do Camunda não encontradas no .env");
        }

        //Configurar Credenciais OAuth
        final OAuthCredentialsProvider credentialsProvider = new OAuthCredentialsProviderBuilder()
                .authorizationServerUrl(authServerUrl)
                .audience(tokenAudience)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();

        //Limpar endereço gRPC (remover https:// e porta se necessário)
        String grpcAddressClean = grpcAddress.replace("https://", "").replace(":443", "");
        String finalGrpcUri = "https://" + grpcAddressClean + ":443";

        System.out.println(">>> A criar conexão ao Camunda 8...");

        //Construir e devolver o cliente
        return ZeebeClient.newClientBuilder()
                .grpcAddress(URI.create(finalGrpcUri))
                .restAddress(URI.create(restAddress))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
