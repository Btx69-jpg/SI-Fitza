package com.camunda.academy.ReceiverTasks.ProgramarProducao;

import com.camunda.classes.Cliente;
import com.camunda.classes.ProgramarProducao.Order;
import com.camunda.classes.ProgramarProducao.OrderDescription;
import com.camunda.classes.RegistoLote.Enums.TypePizza;
import com.camunda.utils.CamundaClientFactory;
import io.camunda.zeebe.client.ZeebeClient;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class ClientOrderSendSimulation {

    public static void main(String[] args) {
        try (final ZeebeClient client = CamundaClientFactory.createClient()) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("\n>>> SIMULAÇÃO DE PEDIDO DO CLIENTE (E-COMMERCE) <<<");
            System.out.println("-----------------------------------------------------");

            System.out.print("Nome do Cliente: ");
            String nomeCliente = scanner.nextLine();

            System.out.print("Email do Cliente: ");
            String mailCliente = scanner.nextLine();

            String clienteId = "CLI-" + UUID.randomUUID().toString().substring(0, 8);
            //String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
            String orderId = "ORD-12345";
            System.out.println("\nEscolha a Pizza:");
            System.out.println("1 - Quatro Queijos");
            System.out.println("2 - Vegetariana");
            System.out.println("3 - Queijo e Fiambre");
            System.out.println("4 - Pepperoni");
            System.out.print("Opção: ");
            int opPizza = Integer.parseInt(scanner.nextLine());

            TypePizza tipoSelecionado = null;
            switch (opPizza) {
                case 1: tipoSelecionado = TypePizza.FOUR_CHESSES; break;
                case 2: tipoSelecionado = TypePizza.VEGETARIAN; break;
                case 3: tipoSelecionado = TypePizza.CHEESE_COLD_CUTS; break;
                case 4: tipoSelecionado = TypePizza.PEPPERONI; break;
                default:
                    System.out.println("Opção inválida, assumindo Pepperoni.");
                    tipoSelecionado = TypePizza.PEPPERONI;
            }

            System.out.print("Quantidade: ");
            int quantidade = Integer.parseInt(scanner.nextLine());

            Cliente clienteObj = new Cliente(clienteId, nomeCliente);
            clienteObj.setMail(mailCliente);

            OrderDescription itemPedido = new OrderDescription(tipoSelecionado, quantidade);

            Order orderObj = new Order(
                    orderId,
                    LocalDate.now().toString(),
                    "NEW",
                    clienteObj,
                    itemPedido
            );

            Map<String, Object> variables = new HashMap<>();

            variables.put("orderId", orderId);
            variables.put("orderData", orderObj);
            variables.put("orderTotalQuantity", quantidade);

            System.out.println("\nA enviar pedido " + orderId + " para o sistema...");

            client.newPublishMessageCommand()
                    .messageName("orderRequest")
                    .correlationKey(orderId)
                    .variables(variables)
                    .send()
                    .join();

            System.out.println(">>> Sucesso! Pedido enviado para a fábrica.");
        } catch (Exception e) {
            System.err.println("Erro ao enviar pedido: " + e.getMessage());
            e.printStackTrace();
        }
    }
}