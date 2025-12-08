package com.camunda.classes.ProgramarProducao;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/**
 * Representa uma **Ordem de Produção (Production Order)**.
 *
 * <p>Esta classe é criada após o processo de planeamento e contém todos os
 * detalhes necessários para a fábrica executar a produção de uma encomenda
 * de cliente, incluindo o seu prazo de conclusão e estado atual.
 */
public class ProductionOrder {
    /**
     * ID Interno único da Ordem de Produção (ex: "PROD-2024-001").
     * Utilizado para rastreio dentro do sistema de gestão da fábrica (MES/ERP).
     */
    private String productionOrderId;
    /**
     * Ligação ao ID da encomenda original do cliente (ex: "ORD-123").
     * Permite ligar o item de produção de volta ao pedido do cliente.
     */
    private String originalOrderId;

    /**
     * Data prevista de conclusão da produção.
     *
     * <p>Utiliza a anotação {@link JsonFormat} para garantir que a data seja
     * serializada/desserializada no padrão "dd-MM-yyyy" (Dia-Mês-Ano).
     */
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate scheduledDate;

    /**
     * O estado atual da Ordem de Produção.
     * Ex: "PLANNED" (Planeada), "READY_TO_START" (Pronta a Iniciar), "IN_PROGRESS" (Em Curso).
     */
    private String status;

    /**
     * Construtor padrão (necessário para serialização/desserialização JSON).
     */
    public ProductionOrder() {}

    /**
     * Construtor para criar uma nova instância de Ordem de Produção.
     *
     * @param productionOrderId O ID interno da ordem de produção.
     * @param originalOrderId O ID da encomenda do cliente associada.
     * @param scheduledDate A data de conclusão prevista.
     * @param status O estado inicial da ordem (ex: "PLANNED").
     */
    public ProductionOrder(String productionOrderId, String originalOrderId, LocalDate scheduledDate, String status) {
        this.productionOrderId = productionOrderId;
        this.originalOrderId = originalOrderId;
        this.scheduledDate = scheduledDate;
        this.status = status;
    }

    /**
     * Obtém o ID interno da Ordem de Produção.
     * @return O {@code productionOrderId}.
     */
    public String getProductionOrderId() { return productionOrderId; }
    /**
     * Define o ID interno da Ordem de Produção.
     * @param productionOrderId O novo ID.
     */
    public void setProductionOrderId(String productionOrderId) { this.productionOrderId = productionOrderId; }

    /**
     * Obtém o ID da encomenda do cliente original.
     * @return O {@code originalOrderId}.
     */
    public String getOriginalOrderId() { return originalOrderId; }
    /**
     * Define o ID da encomenda do cliente original.
     * @param originalOrderId O novo ID.
     */
    public void setOriginalOrderId(String originalOrderId) { this.originalOrderId = originalOrderId; }

    /**
     * Obtém a data de conclusão prevista da produção.
     * @return A data prevista como {@link LocalDate}.
     */
    public LocalDate getScheduledDate() { return scheduledDate; }
    /**
     * Define a data de conclusão prevista da produção.
     * @param scheduledDate A nova data prevista.
     */
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    /**
     * Obtém o estado atual da Ordem de Produção.
     * @return O estado (ex: "PLANNED").
     */
    public String getStatus() { return status; }
    /**
     * Define o estado atual da Ordem de Produção.
     * @param status O novo estado.
     */
    public void setStatus(String status) { this.status = status; }
}
