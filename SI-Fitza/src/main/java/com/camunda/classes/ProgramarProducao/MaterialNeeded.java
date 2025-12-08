package com.camunda.classes.ProgramarProducao;

import com.camunda.classes.RawMaterial;

public class MaterialNeeded {
    private RawMaterial rawMaterial;
    private int quantity;

    public MaterialNeeded(RawMaterial rawMaterial, int quantity) {
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
    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

}
