package com.camunda.classes.ProgramarProducao;

import com.camunda.classes.RawMaterial;
import com.camunda.classes.RegistoLote.Enums.TypePizza;

public class ProductTechnicalSheet {
    private TypePizza productType;
    private String productDescription;
    private MaterialNeeded[] materialNeeded;

    public ProductTechnicalSheet() {}

    public ProductTechnicalSheet(TypePizza productType, String productDescription, MaterialNeeded[] materialNeeded) {
        this.productType = productType;
        this.productDescription = productDescription;
        this.materialNeeded = materialNeeded;
    }

    public TypePizza getProductType() {
        return productType;
    }
    public String getProductDescription() {
        return productDescription;
    }
    public MaterialNeeded[] getMaterialNeeded() {
        return materialNeeded;
    }
}
