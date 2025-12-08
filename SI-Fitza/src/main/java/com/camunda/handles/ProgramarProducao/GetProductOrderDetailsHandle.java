package com.camunda.handles.ProgramarProducao;

import com.camunda.classes.ProgramarProducao.Order;
import com.camunda.utils.LoteUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.Map;

public class GetProductOrderDetailsHandle implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: SOLICITAR ENCOMENDAS] A processar detalhes da encomenda...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            // 1. Converter a variável 'orderData' (JSON) para o objeto Java Order
            if (!variables.containsKey("orderData")) {
                throw new RuntimeException("A variável 'orderData' não foi encontrada!");
            }

            Order order = LoteUtils.getMapper().convertValue(variables.get("orderData"), Order.class);

            System.out.println("   > ID Encomenda Recebida: " + order.getOrderId());
            System.out.println("   > Cliente: " + order.getClientData().getName());
            System.out.println("   > Quantidade de Itens: " + order.getOrderDescription().length);


            client.newCompleteCommand(job.getKey())
                    .send()
                    .join();

            System.out.println(">>> Detalhes da encomenda validados com sucesso.");

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao processar encomenda: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
