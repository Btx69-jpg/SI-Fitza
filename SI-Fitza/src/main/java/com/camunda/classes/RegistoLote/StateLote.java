package com.camunda.classes.RegistoLote;

import com.camunda.classes.RegistoLote.Enums.LoteState;

public class StateLote {
    private LoteState state;
    private String discartReason;

    public StateLote() {}

    public StateLote(LoteState state, String discartReason) {
        this.state = state;
        this.discartReason = discartReason;
    }

    public LoteState getState() {
        return state;
    }

    public String getDiscartReason() {
        return discartReason;
    }

    public void setState(LoteState state) {
        this.state = state;
    }

    public void setDiscartReason(String discartReason) {
        this.discartReason = discartReason;
    }
}
