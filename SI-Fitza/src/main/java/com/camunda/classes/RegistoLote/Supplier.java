package com.camunda.classes.RegistoLote;

public class Supplier {
    public String id;
    public String name;

    public Supplier(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
