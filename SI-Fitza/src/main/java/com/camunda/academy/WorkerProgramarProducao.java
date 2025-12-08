package com.camunda.academy;

import com.camunda.handles.ProgramarProducao.*;
import com.camunda.utils.CamundaClientFactory;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.Topology;

import java.util.Scanner;

public class WorkerProgramarProducao {
    public static void main(String[] args) {
        try (final ZeebeClient client = CamundaClientFactory.createClient()) {

            final Topology topology = client.newTopologyRequest().send().join();
            System.out.println("Conexão com sucesso! Cluster size: " + topology.getClusterSize());

            System.out.println(">>> A REGISTAR WORKERS DO PROCESSO DE PRODUÇÃO...");


            // Passo 1: Receber e Validar a Encomenda
            client.newWorker()
                    .jobType("solicitar_encomendas")
                    .handler(new GetProductOrderDetailsHandle())
                    .name("orderWorker")
                    .timeout(10000)
                    .open();
            System.out.println("   [1] Worker 'solicitar_encomendas' (Validar Pedido) -> ATIVO");

            // Passo 2: Calcular Materiais (Ficha Técnica)
            client.newWorker()
                    .jobType("calculate_materials_needs")
                    .handler(new CalculateRawMaterialNeedsHandle())
                    .name("technicalWorker")
                    .timeout(10000)
                    .open();
            System.out.println("   [2] Worker 'calculate_materials_needs' (Ficha Técnica) -> ATIVO");

            // Passo 3: Verificar Stock
            client.newWorker()
                    .jobType("verificar_stock")
                    .handler(new CheckStockHandle())
                    .name("stockWorker")
                    .timeout(10000)
                    .open();
            System.out.println("   [3] Worker 'verificar_stock' (Armazém) -> ATIVO");

            // Alternativa 3b: Enviar Email ao Fornecedor (Caso falte stock)
            client.newWorker()
                    .jobType("email_fornecedor")
                    .handler(new SendPurchaseOrderMailHandle())
                    .name("emailSupplierWorker")
                    .timeout(10000)
                    .open();
            System.out.println("   [3b] Worker 'email_fornecedor' (Reposição) -> ATIVO");


            // Passo 4: Verificar Estado das Máquinas
            client.newWorker()
                    .jobType("check_machines")
                    .handler(new CheckMachineStatusHandle())
                    .name("machineWorker")
                    .timeout(10000)
                    .open();
            System.out.println("   [4] Worker 'check_machines' (Manutenção) -> ATIVO");

            // Alternativa 4b: Enviar Email à Manutenção (Caso haja avaria)
            client.newWorker()
                    .jobType("email_manutencao")
                    .handler(new ContactMaintenanceEmailHandle())
                    .name("emailMaintWorker")
                    .timeout(10000)
                    .open();
            System.out.println("   [4b] Worker 'email_manutencao' (Alerta) -> ATIVO");

            // Passo 5: Estimar Prazo de Entrega
            client.newWorker()
                    .jobType("estimar_prazo")
                    .handler(new CalculateDeliveryDateHandle())
                    .name("deliveryWorker")
                    .timeout(10000)
                    .open();
            System.out.println("   [5] Worker 'estimar_prazo' (Cálculo Data) -> ATIVO");

            // Passo 6: Registar Ordem de Produção
            client.newWorker()
                    .jobType("registar_ordem")
                    .handler(new RegisterOrderHandle())
                    .name("autoRegisterWorker")
                    .timeout(10000)
                    .open();
            System.out.println("   [6] Worker 'registar_ordem' (Base de Dados) -> ATIVO");

            // Passo 7: Notificar Cliente
            client.newWorker()
                    .jobType("notificar_cliente")
                    .handler(new NotifyClientDeadlineEmailHandle())
                    .name("emailClientWorker")
                    .timeout(10000)
                    .open();
            System.out.println("   [7] Worker 'notificar_cliente' (Confirmação) -> ATIVO");


            System.out.println("\n>>> SISTEMA PRONTO! TODOS OS WORKERS ESTÃO À ESCUTA.");
            System.out.println("    (Podes iniciar o processo no Tasklist agora)");
            System.out.println("    Prime ENTER para encerrar a aplicação...");

            Scanner sc = new Scanner(System.in);
            sc.nextLine();

            System.out.println("A encerrar o worker...");
        } catch (Exception e) {
            System.err.println("Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
