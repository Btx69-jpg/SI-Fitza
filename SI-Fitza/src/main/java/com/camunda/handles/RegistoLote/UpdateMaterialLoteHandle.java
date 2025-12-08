package com.camunda.handles.RegistoLote;

import com.camunda.classes.RawMaterial;
import com.camunda.classes.RawMaterialUsed;
import com.camunda.classes.RegistoLote.Lote;
import com.camunda.classes.Supplier;
import com.camunda.utils.LoteUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Worker do Camunda/Zeebe responsável por **deserializar uma lista de materiais brutos**
 * e anexá-los ao objeto {@link Lote} no contexto do processo.
 * <p>
 * Este Worker é projetado para lidar com a variável de processo {@code "listMaterials"},
 * que se espera ser um array JSON de objetos contendo dados de materiais.
 * </p>
 *
 * <h3>Passos de Execução:</h3>
 * <ol>
 * <li>Obtém a instância do {@link Lote} do contexto do processo usando {@link LoteUtils#getLoteFromJob(ActivatedJob)}.</li>
 * <li>Recupera a variável {@code "listMaterials"} e converte-a para uma {@code List<Map<String, Object>>}.</li>
 * <li>Irá iterar sobre cada mapa (material), mapeando os seus campos para as classes {@link Supplier}, {@link RawMaterial} e {@link RawMaterialUsed}.</li>
 * <li>Adiciona cada {@link RawMaterialUsed} à lista de materiais do objeto {@code Lote}.</li>
 * <li>Completa o Job, enviando o objeto {@code Lote} atualizado de volta para o processo e limpando as variáveis auxiliares ({@code "listMaterials"}, etc.).</li>
 * </ol>
 */
public class UpdateMaterialLoteHandle implements JobHandler {

    /**
     * Lógica de tratamento para o Job.
     *
     * @param client O cliente do Job, usado para completar ou falhar o Job.
     * @param job O Job ativado pelo motor Zeebe.
     */
    @Override
    public void handle(final JobClient client, final ActivatedJob job) {
        try {
            System.out.println(">>> [MATERIAL] A processar lista de materiais...");

            Map<String, Object> variables = job.getVariablesAsMap();

            //Obter o Lote Atual do processo
            Lote lote = LoteUtils.getLoteFromJob(job);

            //Verificar se a lista existe (O nome tem de bater certo com o JSON: "listMaterials")
            if (variables.containsKey("listMaterials")) {
                List<Map<String, Object>> rawList = LoteUtils.getMapper().convertValue(
                        variables.get("listMaterials"),
                        new TypeReference<>() {}
                );

                if (rawList != null) {
                    //Iterar sobre cada material da lista
                    for (Map<String, Object> item : rawList) {

                        String rId = (String) item.getOrDefault("rawMaterialId", "N/A");
                        String rName = (String) item.getOrDefault("materialName", "Desconhecido");

                        Number qtyNum = (Number) item.get("quantity");
                        double quantity = (qtyNum != null) ? qtyNum.doubleValue() : 0.0;

                        String expDateStr = (String) item.get("expirationDate");
                        LocalDate expirationDate;
                        if (expDateStr != null && !expDateStr.isEmpty()) {
                            expirationDate = LocalDate.parse(expDateStr);
                        } else {
                            expirationDate = LocalDate.now().plusYears(1); // Default se vier null
                        }

                        String sId = (String) item.getOrDefault("supplierId", "N/A");
                        String sName = (String) item.getOrDefault("supplierName", "Desconhecido");

                        Supplier supplier = new Supplier(sId, sName);
                        RawMaterial rawMaterial = new RawMaterial(rId, rName, supplier);
                        RawMaterialUsed rawMaterialUsed = new RawMaterialUsed(rawMaterial, expirationDate, quantity);

                        //Adiciona o material ao Lote
                        lote.addMaterialUsed(rawMaterialUsed);
                        System.out.println("   + Material Adicionado: " + rName + " | Qtd: " + quantity);
                    }
                }
            } else {
                System.out.println(">>> AVISO: Variável 'listMaterials' não encontrada.");
            }

            //Prepara o Lote para retorno e limpa variáveis
            Map<String, Object> outputVariables = LoteUtils.wrapLoteVariable(lote);

            outputVariables.put("listMaterials", null);
            outputVariables.put("rawMaterialId", null);
            outputVariables.put("materialName", null);

            //Completa o Job
            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();

            System.out.println(">>> SUCESSO: Materiais atualizados no Lote " + lote.getLoteId());

        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao processar lista de materiais: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
