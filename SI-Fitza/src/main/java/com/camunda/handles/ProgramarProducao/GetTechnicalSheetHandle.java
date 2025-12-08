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

public class GetTechnicalSheetHandle implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: FICHA TÉCNICA] A calcular materiais necessários...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            // 1. Obter a Encomenda
            if (!variables.containsKey("orderData")) {
                throw new RuntimeException("Variável 'orderData' não encontrada!");
            }
            Order order = LoteUtils.getMapper().convertValue(variables.get("orderData"), Order.class);

            // 2. Calcular Materiais (Cruzamento com Ficha Técnica)
            List<MaterialNeeded> totalMaterialsNeeded = new ArrayList<>();

            for (OrderDescription item : order.getOrderDescription()) {
                TypePizza type = item.getTypePizza();
                int quantidadePizzas = item.getQuantity();

                System.out.println("   > Processar: " + type + " (Qtd: " + quantidadePizzas + ")");

                // Simulação de busca à Base de Dados
                ProductTechnicalSheet sheet = getMockTechnicalSheet(type);

                // Multiplicar ingredientes pela quantidade de pizzas
                for (MaterialNeeded mat : sheet.getMaterialNeeded()) {
                    double totalQty = mat.getQuantity() * quantidadePizzas;

                    // Adicionar à lista final
                    RawMaterial rm = mat.getRawMaterial();
                    totalMaterialsNeeded.add(new MaterialNeeded(rm, (int) Math.ceil(totalQty)));
                }
            }

            System.out.println("   > Total de Ingredientes Calculados: " + totalMaterialsNeeded.size());

            // 3. Enviar lista para a próxima etapa (Stock)
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
                    .errorMessage("Erro na ficha técnica: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}


