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

/**
 * {@code GetTechnicalSheetHandle} é um {@link io.camunda.zeebe.client.api.worker.JobHandler}
 * responsável por **obter a Ficha Técnica** de cada produto na encomenda e **calcular
 * a quantidade total de matéria-prima necessária** para satisfazer o pedido.
 *
 * <p>Recebe a variável {@code orderData} (objeto {@link Order}) e calcula as
 * necessidades multiplicando os requisitos unitários da Ficha Técnica (simulada
 * via {@link #getMockTechnicalSheet(TypePizza)}) pela quantidade encomendada.
 * O resultado é arredondado para o inteiro superior ({@code Math.ceil}).
 *
 * <p>A variável de saída é {@code materialsNeededList} ({@code List<MaterialNeeded>}).
 */
public class GetTechnicalSheetHandle implements JobHandler {

    /**
     * Trata a tarefa (Job) ativada do Camunda Zeebe.
     *
     * <p>O fluxo de trabalho principal é:
     * <ol>
     * <li>Obter o objeto {@code Order} da variável de processo {@code orderData}.</li>
     * <li>Iterar sobre cada item da encomenda ({@code OrderDescription}).</li>
     * <li>Para cada item, obter a ficha técnica (simulada) e calcular a quantidade
     * total de matéria-prima (quantidade unitária * quantidade encomendada).</li>
     * <li>Arredondar a quantidade total de matéria-prima para o inteiro superior.</li>
     * <li>Completar a tarefa com a lista final de necessidades, {@code materialsNeededList}.</li>
     * </ol>
     *
     * @param client O cliente do Job para enviar comandos de conclusão ({@code complete}) ou falha ({@code fail}).
     * @param job O Job ativado que contém os detalhes da tarefa e variáveis de entrada.
     * @throws Exception Se a variável {@code 'orderData'} não for encontrada ou se ocorrer um erro na conversão/cálculo.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: FICHA TÉCNICA] A calcular materiais necessários...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            //Obter a Encomenda
            if (!variables.containsKey("orderData")) {
                throw new RuntimeException("Variável 'orderData' não encontrada!");
            }
            Order order = LoteUtils.getMapper().convertValue(variables.get("orderData"), Order.class);

            //Calcular Materiais (Cruzamento com Ficha Técnica)
            List<MaterialNeeded> totalMaterialsNeeded = new ArrayList<>();

            for (OrderDescription item : order.getOrderDescription()) {
                TypePizza type = item.getTypePizza();
                int quantidadePizzas = item.getQuantity();

                System.out.println("   > Processar: " + type + " (Qtd: " + quantidadePizzas + ")");
                ProductTechnicalSheet sheet = getMockTechnicalSheet(type);

                for (MaterialNeeded mat : sheet.getMaterialNeeded()) {
                    double totalQty = mat.getQuantity() * quantidadePizzas;
                    RawMaterial rm = mat.getRawMaterial();
                    totalMaterialsNeeded.add(new MaterialNeeded(rm, (int) Math.ceil(totalQty)));
                }
            }

            System.out.println("   > Total de Ingredientes Calculados: " + totalMaterialsNeeded.size());

            //Enviar lista para a próxima etapa (Stock)
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

    private ProductTechnicalSheet getMockTechnicalSheet(TypePizza type) {
        // Matérias-Primas fictícias
        RawMaterial flour = new RawMaterial("RM-001", "Farinha Tipo 65", null);
        RawMaterial yeast = new RawMaterial("RM-002", "Fermento de Padeiro", null);
        RawMaterial cheese = new RawMaterial("RM-003", "Queijo Mozzarella", null);
        RawMaterial pepperoni = new RawMaterial("RM-004", "Pepperoni Fatiado", null);
        RawMaterial ham = new RawMaterial("RM-005", "Fiambre", null);
        RawMaterial veggies = new RawMaterial("RM-006", "Vegetais Variados", null);
        RawMaterial tomatoSauce = new RawMaterial("RM-007", "Molho de Tomate", null);

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
                        new MaterialNeeded(cheese, 3)
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
                return new ProductTechnicalSheet(type, "Massa Base", new MaterialNeeded[]{
                        new MaterialNeeded(flour, 1),
                        new MaterialNeeded(yeast, 1)
                });
        }
    }
}


