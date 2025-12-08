package com.camunda.handles.ProgramarProducao;

import com.camunda.classes.ProgramarProducao.MaterialNeeded;
import com.camunda.classes.ProgramarProducao.Order;
import com.camunda.classes.ProgramarProducao.OrderDescription;
import com.camunda.classes.ProgramarProducao.ProductTechnicalSheet;
import com.camunda.classes.RawMaterial;
import com.camunda.classes.RegistoLote.Enums.TypePizza;
import com.camunda.utils.LoteUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateRawMaterialNeedsHandle implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: CALCULAR MATERIAIS] A analisar necessidades de produção...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            // 1. Obter a Encomenda das variáveis
            if (!variables.containsKey("orderData")) {
                throw new RuntimeException("A variável 'orderData' não foi encontrada!");
            }
            Order order = LoteUtils.getMapper().convertValue(variables.get("orderData"), Order.class);

            System.out.println("   > Encomenda: " + order.getOrderId());

            // 2. Calcular Materiais (Cruzamento com Ficha Técnica)
            List<MaterialNeeded> totalMaterialsNeeded = new ArrayList<>();

            for (OrderDescription item : order.getOrderDescription()) {
                TypePizza type = item.getTypePizza();
                int quantidadePizzas = item.getQuantity();

                System.out.println("   > A processar item: " + type + " (Qtd: " + quantidadePizzas + ")");

                // Obter a ficha técnica (Simulação)
                ProductTechnicalSheet sheet = getMockTechnicalSheet(type);

                // Calcular totais para este item
                for (MaterialNeeded mat : sheet.getMaterialNeeded()) {
                    // Qtd Unitária * Qtd Encomendada
                    double totalQty = mat.getQuantity() * quantidadePizzas;

                    // Adicionar à lista final
                    // Nota: Numa app real, devias agrupar materiais iguais (ex: somar Farinha de todas as pizzas)
                    RawMaterial rm = mat.getRawMaterial();
                    totalMaterialsNeeded.add(new MaterialNeeded(rm, (int) Math.ceil(totalQty)));
                }
            }

            System.out.println("   > Cálculo concluído. Total de linhas de material: " + totalMaterialsNeeded.size());

            // 3. Enviar lista para a próxima etapa (Verificar Stock)
            Map<String, Object> output = new HashMap<>();
            output.put("materialsNeededList", totalMaterialsNeeded);

            client.newCompleteCommand(job.getKey())
                    .variables(output)
                    .send()
                    .join();

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao calcular materiais: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
