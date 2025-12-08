package com.camunda.utils;

import com.camunda.classes.ProgramarProducao.Order;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public class OrdersUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static void saveOrderToDisk(Order order) {
        try {
            // Definir a pasta alvo
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
