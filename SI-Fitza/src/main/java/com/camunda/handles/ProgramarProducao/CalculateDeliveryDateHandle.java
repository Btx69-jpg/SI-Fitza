package com.camunda.handles.ProgramarProducao;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * {@code CalculateDeliveryDateHandle} é um {@link io.camunda.zeebe.client.api.worker.JobHandler}
 * responsável por **simular e calcular uma data de entrega** para o processo de produção.
 *
 * <p>Esta classe simula a estimativa do prazo de entrega, calculando uma data
 * que está entre **hoje** e **hoje + 14 dias (2 semanas)**.
 *
 * <p>O resultado da data de entrega é retornado ao processo Camunda como uma variável
 * chamada {@code deliveryDate} (String no formato ISO-8601, ex: "2025-12-22").
 */
public class CalculateDeliveryDateHandle implements JobHandler {

    /**
     * Trata a tarefa (Job) ativada do Camunda Zeebe.
     *
     * <p>Executa a seguinte lógica (simulada):
     * <ol>
     * <li>Gera um número aleatório de dias (0 a 14).</li>
     * <li>Calcula a data de entrega estimada (data atual + dias aleatórios).</li>
     * <li>Imprime o resultado na consola.</li>
     * <li>Completa a tarefa com a variável de saída {@code deliveryDate}.</li>
     * </ol>
     * Em caso de erro, a tarefa falha (fail command) e as retentativas são
     * definidas para 0.
     *
     * @param client O cliente do Job para enviar comandos de conclusão ou falha.
     * @param job O Job ativado que contém os detalhes da tarefa e variáveis.
     * @throws Exception Se ocorrer um erro durante o tratamento (embora o erro
     * seja geralmente tratado internamente com o comando {@code fail}).
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [TASK: ESTIMAR PRAZOS] A calcular data de entrega (Simulação)...");

        try {
            Random random = new Random();
            int daysToAdd = random.nextInt(15); // Gera um número de 0 a 14

            LocalDate estimatedDate = LocalDate.now().plusDays(daysToAdd);

            System.out.println("   > Previsão calculada: " + daysToAdd + " dias de produção.");
            System.out.println("   > Data de Entrega: " + estimatedDate.toString());

            // Enviar a variável 'deliveryDate' para o processo
            Map<String, Object> output = new HashMap<>();
            output.put("deliveryDate", estimatedDate.toString());

            client.newCompleteCommand(job.getKey())
                    .variables(output)
                    .send()
                    .join();

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao calcular data: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
