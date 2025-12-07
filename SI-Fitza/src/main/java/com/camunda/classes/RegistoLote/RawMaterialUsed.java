package com.camunda.classes.RegistoLote;

public class RawMaterialUsed {
    private RawMaterial rawMaterial;
    private int quantity;

    public RawMaterialUsed(RawMaterial rawMaterial, int quantidade) {
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
