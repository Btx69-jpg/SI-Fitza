package com.camunda.classes.RegistoLote;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class RawMaterialUsed {
    private RawMaterial rawMaterial;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;
    private int quantity;

    public RawMaterialUsed() {}

    public RawMaterialUsed(RawMaterial rawMaterial, int quantity) {
        this.rawMaterial = rawMaterial;
        this.quantity = quantity;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
