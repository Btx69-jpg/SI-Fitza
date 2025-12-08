package com.camunda.classes.RegistoLote;

import com.camunda.classes.RegistoLote.Enums.LoteState;

public class StateLote {
    private LoteState state;
    private DiscartReason discartReason;

    public StateLote() { }

    public StateLote(DiscartReason discartReason, LoteState state) {
        this.discartReason = discartReason;
        this.state = state;
    }

    public DiscartReason getDiscartReason() {
        return discartReason;
    }

    public LoteState getState() {
        return state;
    }
}
