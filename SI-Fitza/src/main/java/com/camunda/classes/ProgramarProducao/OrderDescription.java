package com.camunda.classes.ProgramarProducao;

import com.camunda.classes.RegistoLote.Enums.TypePizza;

public class OrderDescription {
    private TypePizza typePizza;
    private int quantity;

    public OrderDescription() {}
    public OrderDescription(TypePizza typePizza, int quantity) {
        this.typePizza = typePizza;
        this.quantity = quantity;
    }
    public TypePizza getTypePizza() {
        return typePizza;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setTypePizza(TypePizza typePizza) {
        this.typePizza = typePizza;
    }
}
