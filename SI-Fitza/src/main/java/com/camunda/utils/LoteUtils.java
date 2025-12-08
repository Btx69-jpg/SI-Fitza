package com.camunda.utils;

import com.camunda.classes.RegistoLote.Lote;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.camunda.zeebe.client.api.response.ActivatedJob;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitária centralizada para manipulação e serialização do objeto {@link Lote}.
 * <p>
 * O objetivo desta classe é aplicar o princípio <b>DRY (Don't Repeat Yourself)</b> nos Workers do Camunda.
 * Em vez de configurar o Jackson e converter variáveis manualmente em cada Worker, esta classe
 * fornece métodos estáticos padronizados para:
 * <ul>
 * <li>Recuperar o Lote das variáveis do processo (Deserialização).</li>
 * <li>Preparar o Lote para ser enviado de volta ao processo (Serialização).</li>
 * </ul>
 * </p>
 * <p>
 * <b>Nota Técnica:</b> Utiliza uma instância única (Singleton) do {@link ObjectMapper} configurada
 * com o {@link JavaTimeModule} para lidar corretamente com datas do Java 8 ({@code LocalDate}, {@code LocalDateTime}).
 * </p>
 */
public class LoteUtils {
    /**
     * Instância estática e partilhada do ObjectMapper.
     * <p>
     * O ObjectMapper do Jackson é <b>Thread-Safe</b> após a configuração inicial.
     * Reutilizar esta instância melhora a performance, evitando a criação dispendiosa
     * de novos objetos de mapeamento a cada execução de tarefa.
     * </p>
     */
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    /**
     * Recupera o objeto {@link Lote} a partir das variáveis do Job atual do Zeebe.
     * <p>
     * Este método faz a conversão automática do Map (formato JSON do Camunda) para a classe Java fortemente tipada.
     * É essencial que a estrutura do JSON no Camunda corresponda à estrutura da classe {@code Lote}.
     * </p>
     *
     * @param job O {@link ActivatedJob} recebido no método handle do Worker.
     * @return Uma instância hidratada de {@link Lote} pronta a ser usada/alterada.
     * @throws RuntimeException Se a variável de processo com a chave {@code "lote"} não existir
     * ou se ocorrer um erro de mapeamento (ex: JSON inválido).
     */
    public static Lote getLoteFromJob(ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();

        if (!variables.containsKey("lote")) {
            throw new RuntimeException("Variável 'lote' não encontrada no contexto do processo.");
        }

        // Converte o Map (JSON) para o objeto Java Lote
        return objectMapper.convertValue(variables.get("lote"), Lote.class);
    }

    /**
     * Prepara o objeto {@link Lote} para ser enviado de volta para o motor do Camunda (Zeebe).
     * <p>
     * Envolve o objeto num {@code Map<String, Object>} conforme exigido pela API do cliente Zeebe
     * para o comando {@code completeCommand}.
     * </p>
     *
     * @param lote O objeto {@link Lote} com os dados atualizados.
     * @return Um Map contendo a chave {@code "lote"} e o objeto serializável.
     */
    public static Map<String, Object> wrapLoteVariable(Lote lote) {
        Map<String, Object> loteMap = objectMapper.convertValue(
                lote,
                new TypeReference<>() {}
        );

        Map<String, Object> variables = new HashMap<>();
        variables.put("lote", loteMap);

        return variables;
    }

    /**
     * Obtém a instância do {@link ObjectMapper} configurada.
     * <p>
     * Útil caso seja necessário realizar conversões manuais de outros objetos ou
     * listas que não sejam o objeto principal {@code Lote}.
     * </p>
     *
     * @return A instância estática do ObjectMapper.
     */
    public static ObjectMapper getMapper() {
        return objectMapper;
    }

    public static void saveLoteToDisk(Lote lote) {
        try {
            String folderName = "lotesGerados";
            File directory = new File(folderName);

            if (!directory.exists()) {
                boolean criado = directory.mkdirs();
                if(criado) System.out.println(">>> Pasta 'lotes_gerados' criada com sucesso.");
            }

            String filename = String.format("Lote_%s_%s.json", lote.getLoteId(), lote.getLoteState().getState());
            filename = filename.replaceAll("[/\\\\:*?\"<>|]", "_");

            File file = new File(directory, filename);
            objectMapper.writeValue(file, lote);

            System.out.println(">>> [BACKUP] Ficheiro JSON guardado em: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("ERRO ao guardar ficheiro JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
