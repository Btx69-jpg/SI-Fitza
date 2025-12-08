package com.camunda.classes.ProgramarProducao;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class ProductionOrder {
    private String productionOrderId; // ID Interno (ex: PROD-2024-001)
    private String originalOrderId;   // Ligação à encomenda do cliente (ex: ORD-123)

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate scheduledDate;  // Data prevista de conclusão (calculada no processo)

    private String status;            // Ex: "PLANNED", "READY_TO_START", "IN_PROGRESS"

    public ProductionOrder() {}

    public ProductionOrder(String productionOrderId, String originalOrderId, LocalDate scheduledDate, String status) {
        this.productionOrderId = productionOrderId;
        this.originalOrderId = originalOrderId;
        this.scheduledDate = scheduledDate;
        this.status = status;
    }

    public String getProductionOrderId() { return productionOrderId; }
    public void setProductionOrderId(String productionOrderId) { this.productionOrderId = productionOrderId; }

    public String getOriginalOrderId() { return originalOrderId; }
    public void setOriginalOrderId(String originalOrderId) { this.originalOrderId = originalOrderId; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
