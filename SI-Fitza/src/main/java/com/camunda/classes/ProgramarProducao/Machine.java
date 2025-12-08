package com.camunda.classes.ProgramarProducao;

public class Machine {
    private String id;
    private String name;
    private boolean isWorking; // true = OK, false = Avariada
    private String statusDescription;

    public Machine() {}

    public Machine(String id, String name) {
        this.id = id;
        this.name = name;
        this.isWorking = true; // Por defeito, começa a funcionar
        this.statusDescription = "OPERACIONAL";
    }

    public void setBroken(String reason) {
        this.isWorking = false;
        this.statusDescription = reason;
    }

    public boolean isWorking() { return isWorking; }
    public String getName() { return name; }
    public String getStatusDescription() { return statusDescription; }

    @Override
    public String toString() {
        return name + " (" + id + "): " + statusDescription;
    }
}
