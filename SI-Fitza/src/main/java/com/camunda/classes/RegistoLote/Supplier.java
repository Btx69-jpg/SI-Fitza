package com.camunda.classes.RegistoLote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Supplier {
    public String supplierId;
    @JsonProperty("supplierName")
    public String name;

    public Supplier() {}

    public Supplier(String supplierId, String name) {
        this.supplierId = supplierId;
        this.name = name;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
