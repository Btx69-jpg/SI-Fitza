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
 * {@code CalculateRawMaterialNeedsHandle} é um {@link io.camunda.zeebe.client.api.worker.JobHandler}
 * responsável por **calcular a quantidade total de matéria-prima necessária** para atender
 * a uma determinada encomenda ({@code Order}).
 *
 * <p>O cálculo é feito cruzando cada item da encomenda com a sua respetiva
 * Ficha Técnica de Produto ({@code ProductTechnicalSheet} - simulada), somando as
 * necessidades totais e arredondando para cima ({@code Math.ceil}).
 *
 * <p>A variável de entrada esperada é {@code orderData} (objeto {@code Order}).
 * A variável de saída é {@code materialsNeededList} ({@code List<MaterialNeeded>}).
 */
public class CalculateRawMaterialNeedsHandle implements JobHandler {

    /**
     * Trata a tarefa (Job) ativada do Camunda Zeebe.
     *
     * <p>O fluxo de trabalho principal é:
     * <ol>
     * <li>Obter o objeto {@code Order} da variável de processo {@code orderData}.</li>
     * <li>Iterar sobre cada item da encomenda ({@code OrderDescription}).</li>
     * <li>Para cada item, obter a ficha técnica ({@link #getMockTechnicalSheet(TypePizza)}),
     * calcular a quantidade total de matéria-prima (quantidade unitária * quantidade encomendada)
     * e arredondar para o inteiro superior.</li>
     * <li>Completar a tarefa com a lista final de necessidades, {@code materialsNeededList}.</li>
     * </ol>
     * Em caso de exceção (incluindo a ausência de {@code orderData}), a tarefa falha.
     *
     * @param client O cliente do Job para enviar comandos de conclusão ({@code complete}) ou falha ({@code fail}).
     * @param job O Job ativado que contém os detalhes da tarefa e variáveis de entrada.
     * @throws Exception Se ocorrer um erro interno, embora o erro seja tratado
     * com o comando {@code fail}.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: CALCULAR MATERIAIS] A analisar necessidades de produção...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            //Obter a Encomenda das variáveis
            if (!variables.containsKey("orderData")) {
                throw new RuntimeException("A variável 'orderData' não foi encontrada!");
            }
            Order order = LoteUtils.getMapper().convertValue(variables.get("orderData"), Order.class);

            System.out.println("   > Encomenda: " + order.getOrderId());

            //Calcular Materiais (Cruzamento com Ficha Técnica)
            List<MaterialNeeded> totalMaterialsNeeded = new ArrayList<>();

            for (OrderDescription item : order.getOrderDescription()) {
                TypePizza type = item.getTypePizza();
                int quantidadePizzas = item.getQuantity();

                System.out.println("   > A processar item: " + type + " (Qtd: " + quantidadePizzas + ")");

                ProductTechnicalSheet sheet = getMockTechnicalSheet(type);

                // Calcular totais para este item
                for (MaterialNeeded mat : sheet.getMaterialNeeded()) {
                    double totalQty = mat.getQuantity() * quantidadePizzas;

                    RawMaterial rm = mat.getRawMaterial();
                    totalMaterialsNeeded.add(new MaterialNeeded(rm, (int) Math.ceil(totalQty)));
                }
            }

            System.out.println("   > Cálculo concluído. Total de linhas de material: " + totalMaterialsNeeded.size());

            //Enviar lista para a próxima etapa (Verificar Stock)
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

    /**
     * Simula a recuperação de uma Ficha Técnica de Produto ({@code ProductTechnicalSheet})
     * com base no tipo de pizza ({@code TypePizza}).
     *
     * <p>Este é um método de *mock* para fins de demonstração, substituindo uma
     * pesquisa real a uma base de dados de receitas.
     *
     * @param type O tipo de pizza para o qual a ficha técnica é necessária.
     * @return Uma {@code ProductTechnicalSheet} simulada contendo a lista de
     * matérias-primas e quantidades unitárias.
     */
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
