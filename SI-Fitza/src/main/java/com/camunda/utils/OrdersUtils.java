package com.camunda.utils;

import com.camunda.classes.ProgramarProducao.Order;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

/**
 * **OrdersUtils**
 *
 * <p>Classe de utilidade responsável por funcionalidades relacionadas com a manipulação
 * de objetos {@link Order}, como a sua persistência simulada em disco (serialização JSON).
 */
public class OrdersUtils {

    /**
     * Instância estática e final do {@code ObjectMapper} (Jackson) configurada para
     * lidar corretamente com datas do Java 8 ({@code LocalDate}) e ignorar propriedades
     * desconhecidas durante a desserialização.
     */
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Simula a persistência de um objeto {@link Order} guardando-o como um ficheiro
     * JSON no sistema de ficheiros local, dentro da pasta "encomendasGeradas".
     *
     * <p>Este método cria a pasta se necessário e utiliza o {@code orderId} no nome do ficheiro.
     *
     * @param order O objeto {@link Order} a ser serializado e guardado.
     */
    public static void saveOrderToDisk(Order order) {
        try {
            String folderName = "encomendasGeradas";
            File directory = new File(folderName);

            // Criar diretório se não existir
            if (!directory.exists()) {
                boolean criado = directory.mkdirs();
                if (criado) System.out.println(">>> Pasta '" + folderName + "' criada com sucesso.");
            }

            // Formatar nome do ficheiro (ex: Order_ORD-12345.json)
            String safeId = order.getOrderId().replaceAll("[/\\\\:*?\"<>|]", "_");
            String filename = String.format("Order_%s.json", safeId);

            File file = new File(directory, filename);
            objectMapper.writeValue(file, order);

            System.out.println(">>> [BACKUP] Encomenda guardada em JSON: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("ERRO ao guardar JSON da Encomenda: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
