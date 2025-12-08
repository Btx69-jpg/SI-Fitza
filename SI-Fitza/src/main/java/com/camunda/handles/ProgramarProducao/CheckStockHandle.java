package com.camunda.handles.ProgramarProducao;

import com.camunda.classes.ProgramarProducao.MaterialNeeded;
import com.camunda.utils.LoteUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * {@code CheckStockHandle} é um {@link io.camunda.zeebe.client.api.worker.JobHandler}
 * responsável por **simular a verificação de stock** da matéria-prima necessária
 * para satisfazer a encomenda.
 *
 * <p>Recebe a lista de necessidades calculada na tarefa anterior ({@code materialsNeededList})
 * e, através de uma simulação aleatória (80% de chance de ter stock), determina se
 * todos os materiais estão disponíveis.
 *
 * <p>A variável de saída essencial é {@code isStockAvailable} (Boolean). Se o stock
 * estiver em falta, envia também o relatório {@code missingMaterialsReport}.
 */
public class CheckStockHandle implements JobHandler {

    /**
     * Trata a tarefa (Job) ativada do Camunda Zeebe.
     *
     * <p>O fluxo de trabalho é:
     * <ol>
     * <li>Recupera a lista de {@code MaterialNeeded} da variável de processo.</li>
     * <li>Itera sobre cada item e, através de um valor aleatório, simula a disponibilidade
     * no armazém ({@code itemInStock} = {@code random.nextDouble() > 0.2}).</li>
     * <li>Se um item estiver em falta, {@code isStockAvailable} é definido como {@code false}
     * e o item é adicionado ao {@code missingItems} report.</li>
     * <li>Completa a tarefa, enviando o booleano de disponibilidade e, se necessário,
     * o relatório de itens em falta.</li>
     * </ol>
     *
     * @param client O cliente do Job para enviar comandos de conclusão ({@code complete}) ou falha ({@code fail}).
     * @param job O Job ativado que contém os detalhes da tarefa e variáveis de entrada.
     * @throws Exception Se ocorrer um erro durante a desserialização ou processamento.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> A verificar disponibilidade de stock...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            // Recuperar a lista calculada na tarefa anterior
            List<MaterialNeeded> requiredList = LoteUtils.getMapper().convertValue(
                    variables.get("materialsNeededList"),
                    new TypeReference<>() {}
            );

            boolean isStockAvailable = true;
            StringBuilder missingItems = new StringBuilder();
            Random random = new Random();


            for (MaterialNeeded item : requiredList) {
                boolean itemInStock = random.nextDouble() > 0.2;

                if (!itemInStock) {
                    isStockAvailable = false;
                    missingItems.append(item.getRawMaterial().getName())
                            .append(" (Qtd: ").append(item.getQuantity()).append("); ");
                    System.out.println("    EM FALTA: " + item.getRawMaterial().getName());
                } else {
                    System.out.println("   Disponível: " + item.getRawMaterial().getName());
                }
            }

            Map<String, Object> output = new HashMap<>();
            output.put("isStockAvailable", isStockAvailable);

            if (!isStockAvailable) {
                output.put("missingMaterialsReport", missingItems.toString());
            }

            client.newCompleteCommand(job.getKey())
                    .variables(output)
                    .send()
                    .join();

            System.out.println(">>>  Verificação concluída. Stock disponível? " + isStockAvailable);

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey()).retries(0).errorMessage(e.getMessage()).send().join();
        }
    }
}
