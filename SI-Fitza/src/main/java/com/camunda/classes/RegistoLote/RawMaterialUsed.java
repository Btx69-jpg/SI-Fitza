package com.camunda.classes.RegistoLote;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class RawMaterialUsed {
    private RawMaterial rawMaterial;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;
    private double quantity;

    public RawMaterialUsed() {}

    public RawMaterialUsed(RawMaterial rawMaterial, LocalDate expirationDate, double quantity) {
        this.rawMaterial = rawMaterial;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public double getQuantity() {
        return quantity;
    }

    public LocalDate getExpirationDate() { return expirationDate; }
}
