package com.camunda.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cliente {
    private String clienteId;
    @JsonProperty("clienteName")
    private String name;
    private String mail;

    public Cliente() {}

    public Cliente(String clienteId, String name) {
        this.clienteId = clienteId;
        this.name = name;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
}