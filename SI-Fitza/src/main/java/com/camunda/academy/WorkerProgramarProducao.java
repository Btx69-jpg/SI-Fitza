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

            // Passo 1: Receber e Validar Encomenda
            client.newWorker()
                    .jobType("solicitar_encomenda")
                    .handler(new GetProductOrderDetailsHandle())
                    .name("orderWorker")
                    .timeout(10000)
                    .open();
            System.out.println(">>> Worker 'solicitar_encomendas' Ativo.");

            // Passo 2: Calcular Materiais (Ficha Técnica)
            client.newWorker()
                    .jobType("calculate_materials_needs") // ou "ficha_produto", confirma o teu BPMN
                    .handler(new CalculateRawMaterialNeedsHandle())
                    .name("technicalWorker")
                    .timeout(10000)
                    .open();
            System.out.println(">>> Worker 'calculate_materials_needs' Ativo.");

            // Passo 3: Verificar Stock no Armazém
            client.newWorker()
                    .jobType("verificar_stock")
                    .handler(new CheckStockHandle())
                    .name("stockWorker")
                    .timeout(10000)
                    .open();
            System.out.println(">>> Worker 'verificar_stock' Ativo.");

            // Passo 4: Verificar Estado das Máquinas (5 Máquinas)
            client.newWorker()
                    .jobType("check_machines")
                    .handler(new CheckMachineStatusHandle())
                    .name("machineWorker")
                    .timeout(10000)
                    .open();
            System.out.println(">>> Worker 'check_machines' Ativo.");

            // Passo 5: Estimar Prazo de Entrega
            client.newWorker()
                    .jobType("estimar_prazo")
                    .handler(new CalculateDeliveryDateHandle())
                    .name("deliveryWorker")
                    .timeout(10000)
                    .open();
            System.out.println(">>> Worker 'estimar_prazo' Ativo.");

            // Passo 6 (Opcional): Guardar Ordem na BD (se implementaste)
            // client.newWorker()
            //         .jobType("store_order")
            //         .handler(new SaveProductionOrderHandle())
            //         .name("dbWorker")
            //         .open();

            // --------------------------------------------------------
            // 2. REGISTO DOS WORKERS DE EMAIL E NOTIFICAÇÕES
            // --------------------------------------------------------

            // Enviar Email ao Cliente (Prazo)
            client.newWorker()
                    .jobType("notificar_cliente")
                    .handler(new NotifyClientDeadlineEmailHandle())
                    .name("emailClientWorker")
                    .timeout(10000)
                    .open();
            System.out.println(">>> Worker 'notificar_cliente' Ativo.");

            // Enviar Email ao Fornecedor (Falta de Stock)
            client.newWorker()
                    .jobType("email_fornecedor")
                    .handler(new SendPurchaseOrderMailHandle())
                    .name("emailSupplierWorker")
                    .timeout(10000)
                    .open();
            System.out.println(">>> Worker 'email_fornecedor' Ativo.");

            // Enviar Email à Manutenção (Máquina Avariada)
            client.newWorker()
                    .jobType("email_manutencao")
                    .handler(new ContactMaintenanceEmailHandle())
                    .name("emailMaintWorker")
                    .timeout(10000)
                    .open();


            System.out.println(">>> Worker 'email_manutencao' Ativo.");
            System.out.println("\n [SISTEMA A CORRER] O worker está à espera de tarefas.");
            System.out.println(" Prime ENTER para parar a aplicação e sair...");

            Scanner sc = new Scanner(System.in);
            sc.nextLine();

            System.out.println("A encerrar o worker...");
        } catch (Exception e) {
            System.err.println("Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
