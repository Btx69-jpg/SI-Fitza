package com.camunda.classes.RegistoLote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RawMaterial {
    private String rawMaterialId;
    @JsonProperty("materialName")
    private String name;
    private Supplier supplier;

    public RawMaterial() {}

    public RawMaterial(String rawMaterialId, String name, Supplier supplier) {
        this.rawMaterialId = rawMaterialId;
        this.name = name;
        this.supplier = supplier;
    }

    public String getRawMaterialId() {
        return rawMaterialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
