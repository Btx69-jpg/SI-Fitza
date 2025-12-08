package com.camunda.academy.ReceiverTasks.RegistoLote;

import com.camunda.utils.CamundaClientFactory;
import io.camunda.zeebe.client.ZeebeClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Classe principal para a **Simulação de Resposta do Laboratório** (Sistema Externo).
 * <p>
 * Esta aplicação é um cliente externo simples que simula o sistema do Laboratório
 * a devolver o resultado de uma análise de amostra ao processo Camunda/Zeebe.
 * </p>
 *
 * <h3>Mecanismo de Comunicação:</h3>
 * O sistema utiliza o comando {@code newPublishMessageCommand()} do Zeebe para
 * **publicar uma mensagem** (do tipo `Message Event`) no motor de processo.
 * A correlação da mensagem com a instância de processo correta é feita através:
 * <ul>
 * <li>**Message Name:** {@code "laboratoryResponse"} (deve corresponder ao nome do Evento de Mensagem no BPMN).</li>
 * <li>**Correlation Key:** O ID do Lote fornecido pelo utilizador, que deve corresponder
 * a uma variável de processo na instância à espera da mensagem.</li>
 * </ul>
 *
 * <h3>Variáveis de Saída:</h3>
 * <ul>
 * <li>{@code labResult} (Boolean): Resultado da análise (`true` se Aprovado, `false` se Rejeitado).</li>
 * <li>{@code rejectionReason} (String, Opcional): Motivo do descarte, se {@code labResult} for `false`.</li>
 * </ul>
 */
public class LaboratoryResponse {

    /**
     * Ponto de entrada da aplicação de simulação.
     * <p>
     * Interage com o utilizador para recolher o ID do Lote e o resultado da análise,
     * e publica a mensagem correspondente no cluster Zeebe.
     * </p>
     *
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {

        // Usa try-with-resources para garantir o fecho do cliente Zeebe
        try (final ZeebeClient client = CamundaClientFactory.createClient()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n>>> SIMULAÇÃO LABORATORIAL (SISTEMA EXTERNO) <<<");

            //Recolha de Dados
            System.out.print("Insira o ID do Lote (verifique o email recebido): ");
            String loteIdInput = scanner.nextLine();

            System.out.print("Resultado da Análise? (S = Aprovado / N = Rejeitado): ");
            String res = scanner.nextLine();
            boolean isApproved = res.equalsIgnoreCase("s");

            Map<String, Object> variables = new HashMap<>();
            variables.put("labResult", isApproved);

            if (!isApproved) {
                System.out.print("Motivo da rejeição: ");
                String motivo = scanner.nextLine();
                variables.put("rejectionReason", motivo);
            }

            //Publicação da Mensagem (Signal)
            System.out.println("A enviar resposta para o Camunda...");

            client.newPublishMessageCommand()
                    .messageName("laboratoryResponse")
                    .correlationKey(loteIdInput)
                    .variables(variables)
                    .send()
                    .join();

            System.out.println(">>> Sucesso! O processo do lote " + loteIdInput + " deve ter avançado.");
        } catch (Exception e) {
            System.err.println("Erro ao conectar ou enviar mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }
}