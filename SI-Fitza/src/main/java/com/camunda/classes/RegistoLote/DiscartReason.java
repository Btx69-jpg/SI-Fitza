package com.camunda.classes.RegistoLote;

import com.camunda.classes.RegistoLote.Enums.ActorDiscartLote;

public class DiscartReason {
    private ActorDiscartLote actor;
    private String reason;

    public DiscartReason() {}

    public DiscartReason(String reason, ActorDiscartLote actor) {
        this.reason = reason;
        this.actor = actor;
    }

    public String getReason() {
        return reason;
    }

    public ActorDiscartLote getActor() {
        return actor;
    }
}
