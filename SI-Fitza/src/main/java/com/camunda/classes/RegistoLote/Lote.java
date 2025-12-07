package com.camunda.classes.RegistoLote;

import com.camunda.classes.RegistoLote.Enums.LoteState;
import com.camunda.classes.RegistoLote.Enums.TypePizza;

//TODO: Deve ter uma variavel para os dados das maquinas (que vamos buscar numa service Task)
public class Lote {
    private String loteId;
    private LoteState loteState;
    private TypePizza typePizza;
    private float producedQuantity;
    private boolean isOrder;
    private Cliente cliente;
    private RawMaterialUsed[] rawMaterialUsed;
    private CleaningLine[] cleaningLine;

    public Lote() {}

    public Lote(String loteId, TypePizza typePizza, boolean isOrder,
                float producedQuantity, Cliente cliente,
                RawMaterialUsed[] rawMaterialUsed, CleaningLine[] cleaningLine
    ) {
        this.loteId = loteId;
        this.loteState = LoteState.BLOCKED;
        this.typePizza = typePizza;
        this.isOrder = isOrder;
        this.producedQuantity = producedQuantity;
        this.cliente = cliente;
        this.rawMaterialUsed = rawMaterialUsed;
        this.cleaningLine = cleaningLine;
    }

    public String getLoteId() {
        return loteId;
    }

    public TypePizza getTypePizza() {
        return typePizza;
    }

    public void setTypePizza(TypePizza typePizza) {
        this.typePizza = typePizza;
    }

    public float getProducedQuantity() {
        return producedQuantity;
    }

    public void setProducedQuantity(int producedQuantity) {
        this.producedQuantity = producedQuantity;
    }

    public boolean isOrder() {
        return isOrder;
    }

    public void setOrder(boolean order) {
        isOrder = order;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LoteState getLoteState() {
        return loteState;
    }

    public void setLoteState(LoteState loteState) {
        this.loteState = loteState;
    }

    public CleaningLine[] getCleaningLine() { return cleaningLine;}

    public void setCleaningLine(CleaningLine[] cleaningLine) {
        this.cleaningLine = cleaningLine;
    }

    public RawMaterialUsed[] getRawMaterialUsed() {
        return rawMaterialUsed;
    }

    public void setRawMaterialUsed(RawMaterialUsed[] rawMaterialUsed) {
        this.rawMaterialUsed = rawMaterialUsed;
    }
}
