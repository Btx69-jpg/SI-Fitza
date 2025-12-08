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

            registerWorkers(client);

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

    private static void registerWorkers(ZeebeClient client) {
        // [1] Validar a Encomenda
        client.newWorker()
                .jobType("requestOrders")
                .handler(new GetProductOrderDetailsHandle())
                .name("requestOrdersWorker")
                .timeout(10000).open();
        System.out.println("[1] Worker 'requestOrders' -> ATIVO");

        // [2] Obter Ficha Técnica (Cálculo de Necessidades)
        client.newWorker()
                .jobType("obtainTechnicalDataSheets") // Verifica se no BPMN está este nome!
                .handler(new CalculateRawMaterialNeedsHandle())
                .name("obtainTechnicalDataSheetsWorker")
                .timeout(10000).open();
        System.out.println("[2] Worker 'obtainTechnicalDataSheets' -> ATIVO");

        // [3] Verificar Stock
        client.newWorker()
                .jobType("checkStockLevels")
                .handler(new CheckStockHandle())
                .name("checkStockLevelsWorker")
                .timeout(10000).open();
        System.out.println("[3] Worker 'checkStockLevels' -> ATIVO");

        client.newWorker()
                .jobType("calculateMaterialsNeeds")
                .handler(new CalculateRawMaterialNeedsHandle())
                .name("calculateMaterialsNeedsWorker")
                .timeout(10000).open();
        System.out.println("[3] Worker 'calculateMaterialsNeeds' -> ATIVO");

        // [3b] Enviar Email Fornecedor (Reposição)
        client.newWorker()
                .jobType("issueReplacement")
                .handler(new SendPurchaseOrderMailHandle())
                .name("issueReplacementWorker")
                .timeout(10000).open();
        System.out.println("[3b] Worker 'issueReplacement' -> ATIVO");

        // [4] Verificar Máquinas
        client.newWorker()
                .jobType("checkEquipment")
                .handler(new CheckMachineStatusHandle())
                .name("checkEquipmentWorker")
                .timeout(10000).open();
        System.out.println("[4] Worker 'checkEquipment' -> ATIVO");

        // [4b] Contactar Manutenção
        client.newWorker()
                .jobType("contactMaintenance")
                .handler(new ContactMaintenanceEmailHandle())
                .name("contactMaintenanceWorker")
                .timeout(10000).open();
        System.out.println("[4b] Worker 'contactMaintenance' -> ATIVO");

        // [5] Estimar Prazo
        client.newWorker()
                .jobType("estimateDelivery")
                .handler(new CalculateDeliveryDateHandle())
                .name("deliveryWorker")
                .timeout(10000).open();
        System.out.println("[5] Worker 'estimateDelivery' -> ATIVO");

        // [6] Registar Ordem
        client.newWorker()
                .jobType("registerOrder")
                .handler(new RegisterOrderHandle())
                .name("registerOrderWorker") // Corrigi o nome aqui (estava copiado do delivery)
                .timeout(10000).open();
        System.out.println("[6] Worker 'registerOrder' -> ATIVO");

        // [7] Notificar Cliente
        client.newWorker()
                .jobType("notifyClient")
                .handler(new NotifyClientDeadlineEmailHandle())
                .name("notifyClientWorker")
                .timeout(10000).open();
        System.out.println("[7] Worker 'notifyClient' -> ATIVO");
    }
}