package com.camunda.academy.ReceiverTasks;

import com.camunda.utils.CamundaClientFactory;
import io.camunda.zeebe.client.ZeebeClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LaboratoryResponse {
    public static void main(String[] args) {
        try (final ZeebeClient client = CamundaClientFactory.createClient()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n>>> SIMULAÇÃO LABORATORIAL (SISTEMA EXTERNO) <<<");

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