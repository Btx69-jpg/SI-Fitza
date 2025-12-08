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

/**
 * **Simulador de Envio de Pedido de Cliente (ClientOrderSendSimulation)**.
 *
 * <p>Esta classe simula um sistema de E-commerce ou um serviço externo que capta
 * os dados de uma encomenda do utilizador através da consola e publica uma
 * mensagem no Camunda Zeebe para iniciar o processo de Produção.
 *
 * <p>A mensagem publicada usa o nome {@code "orderRequest"} e é correlacionada
 * pelo {@code orderId}.
 */
public class ClientOrderSendSimulation {

    /**
     * Ponto de entrada da aplicação de simulação.
     *
     * <p>Permite ao utilizador inserir o nome, email, tipo de pizza e quantidade
     * desejada, criando os objetos de modelo {@link Cliente}, {@link OrderDescription}
     * e {@link Order}.
     *
     * <p>Finalmente, publica a mensagem {@code "orderRequest"} no Zeebe, iniciando
     * o processo BPMN.
     *
     * @param args Argumentos de linha de comando (não utilizados).
     */
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