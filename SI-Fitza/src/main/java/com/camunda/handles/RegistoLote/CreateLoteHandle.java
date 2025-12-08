package com.camunda.handles.RegistoLote;

import com.camunda.classes.Cliente;
import com.camunda.classes.RegistoLote.Enums.TypePizza;
import com.camunda.classes.RegistoLote.Lote;
import com.camunda.utils.LoteUtils;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.Map;

/**
 * Worker do Camunda/Zeebe responsável por **criar a instância inicial do objeto de domínio {@link Lote}**
 * a partir das variáveis de processo.
 * <p>
 * Este Worker é tipicamente o primeiro a ser executado no processo de produção de um Lote.
 * Ele deserializa os dados de entrada (ID, tipo de pizza, quantidade, etc.) e encapsula-os no objeto {@code Lote},
 * preparando o contexto para as etapas subsequentes do processo.
 * </p>
 *
 * <h3>Variáveis de Entrada Essenciais:</h3>
 * <ul>
 * <li>{@code loteId} (String): Identificador único do lote.</li>
 * <li>{@code typePizza} (String): Tipo de produto (pizza) a ser produzido, correspondente ao Enum {@link TypePizza}.</li>
 * <li>{@code producedQuantity} (Number): Quantidade de produto (float) a ser produzida.</li>
 * <li>{@code isOrder} (Boolean): Indica se o Lote está a ser produzido sob encomenda.</li>
 * </ul>
 * <p>
 * Se {@code isOrder} for verdadeiro, são lidas as variáveis {@code clienteId} e {@code clienteName} para construir o objeto {@link Cliente} associado.
 * </p>
 */
public class CreateLoteHandle implements JobHandler {

    /**
     * Lógica de tratamento para o Job. Cria a instância do Lote e a insere de volta
     * nas variáveis do processo sob a chave "lote".
     *
     * @param client O cliente do Job, usado para completar ou falhar o Job.
     * @param job O Job ativado pelo motor Zeebe.
     * @throws Exception Se ocorrer um erro durante a conversão de tipos ou a criação do Lote.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println(">>> WORKER INICIADO: A criar Lote...");

        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            String loteId = (String) variables.get("loteId");
            String typePizzaStr = (String) variables.get("typePizza");
            TypePizza typePizza = null;

            //Converter String para Enum TypePizza
            if (typePizzaStr != null) {
                try {
                    typePizza = TypePizza.valueOf(typePizzaStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("Tipo inválido: " + typePizzaStr);
                }
            }

            //Converter Number para float
            Number qtyInput = (Number) variables.get("producedQuantity");
            float producedQuantity = (qtyInput != null) ? qtyInput.floatValue() : 0.0f;

            Boolean isOrder = (Boolean) variables.get("isOrder");
            Cliente clienteObj = null;

            if (isOrder) {
                String cId = (String) variables.get("clienteId");
                String cName = (String) variables.get("clienteName");

                if (cId != null && !cId.isEmpty() && cName != null) {
                    clienteObj = new Cliente(cId, cName);
                    System.out.println("Cliente associado: " + cName);
                }
            }

            Lote novoLote = new Lote(
                    loteId,
                    typePizza,
                    isOrder,
                    producedQuantity,
                    clienteObj
            );

            //Envolver o Lote para ser enviado de volta ao processo
            Map<String, Object> outputVariables = LoteUtils.wrapLoteVariable(novoLote);

            //Limpar variáveis de entrada (opcional, mas recomendado para limpeza do contexto)
            outputVariables.put("typePizza", null);
            outputVariables.put("producedQuantity", null);
            outputVariables.put("isOrder", null);
            outputVariables.put("clienteId", null);
            outputVariables.put("clienteName", null);

            //Completar o Job
            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();

            System.out.println(">>> SUCESSO: Lote criado com Cliente: " + (clienteObj != null ? "SIM" : "NÃO"));
        } catch (Exception e) {
            System.err.println("Erro ao criar lote: " + e.getMessage());
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Falha ao converter ou criar Lote: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
