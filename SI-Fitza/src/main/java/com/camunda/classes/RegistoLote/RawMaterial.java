package com.camunda.classes.RegistoLote;

public class RawMaterial {
    private String id;
    private String name;
    private Supplier supplier;

    public RawMaterial(String id, String name, Supplier supplier) {
        this.id = id;
        this.name = name;
        this.supplier = supplier;

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
