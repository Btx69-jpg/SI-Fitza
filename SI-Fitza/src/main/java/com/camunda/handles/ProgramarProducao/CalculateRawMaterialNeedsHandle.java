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
                // AQUI É ONDE O ERRO OCORRIA: O método getMockTechnicalSheet agora existe lá em baixo
                ProductTechnicalSheet sheet = getMockTechnicalSheet(type);

                // Calcular totais para este item
                for (MaterialNeeded mat : sheet.getMaterialNeeded()) {
                    // Qtd Unitária * Qtd Encomendada
                    double totalQty = mat.getQuantity() * quantidadePizzas;

                    // Adicionar à lista final
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


    private ProductTechnicalSheet getMockTechnicalSheet(TypePizza type) {
        // Ingredientes Base
        RawMaterial flour = new RawMaterial("RM-001", "Farinha Tipo 65", null);
        RawMaterial yeast = new RawMaterial("RM-002", "Fermento de Padeiro", null);
        RawMaterial cheese = new RawMaterial("RM-003", "Queijo Mozzarella", null);
        RawMaterial pepperoni = new RawMaterial("RM-004", "Pepperoni Fatiado", null);
        RawMaterial ham = new RawMaterial("RM-005", "Fiambre", null);
        RawMaterial veggies = new RawMaterial("RM-006", "Vegetais Variados", null);
        RawMaterial tomatoSauce = new RawMaterial("RM-007", "Molho de Tomate", null);

        // Definição das Receitas
        switch (type) {
            case PEPPERONI:
                return new ProductTechnicalSheet(type, "Pizza Pepperoni", new MaterialNeeded[]{
                        new MaterialNeeded(flour, 1),
                        new MaterialNeeded(tomatoSauce, 1),
                        new MaterialNeeded(cheese, 1),
                        new MaterialNeeded(pepperoni, 2)
                });
            case FOUR_CHESSES:
                return new ProductTechnicalSheet(type, "Pizza 4 Queijos", new MaterialNeeded[]{
                        new MaterialNeeded(flour, 1),
                        new MaterialNeeded(tomatoSauce, 1),
                        new MaterialNeeded(cheese, 3) // Dose tripla de queijo
                });
            case VEGETARIAN:
                return new ProductTechnicalSheet(type, "Pizza Vegetariana", new MaterialNeeded[]{
                        new MaterialNeeded(flour, 1),
                        new MaterialNeeded(tomatoSauce, 1),
                        new MaterialNeeded(veggies, 2)
                });
            case CHEESE_COLD_CUTS:
                return new ProductTechnicalSheet(type, "Pizza Fiambre", new MaterialNeeded[]{
                        new MaterialNeeded(flour, 1),
                        new MaterialNeeded(tomatoSauce, 1),
                        new MaterialNeeded(cheese, 1),
                        new MaterialNeeded(ham, 2)
                });
            default:
                // Base Genérica
                return new ProductTechnicalSheet(type, "Massa Base", new MaterialNeeded[]{
                        new MaterialNeeded(flour, 1),
                        new MaterialNeeded(yeast, 1)
                });
        }
    }
}
