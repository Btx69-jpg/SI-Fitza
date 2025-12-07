package com.camunda.serviceTask;

@Component
public class CreateLote  {

    @ZeeWoerker(type = "armazenar_lote", autoComplete = true)
    public void createLote() {

    }
}