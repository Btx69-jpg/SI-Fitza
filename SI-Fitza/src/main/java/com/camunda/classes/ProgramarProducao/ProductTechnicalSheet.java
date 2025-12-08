package com.camunda.classes.ProgramarProducao;

import com.camunda.classes.RegistoLote.Enums.TypePizza;

/**
 * Representa a **Ficha Técnica de um Produto** (Receita), que detalha os materiais
 * e quantidades unitárias necessárias para produzir uma única unidade do produto.
 *
 * <p>Esta classe é essencial para o cálculo das necessidades totais de matéria-prima
 * ao cruzar as quantidades da encomenda com os requisitos unitários de produção.
 */
public class ProductTechnicalSheet {
    /**
     * O tipo de produto (ex: PEPPERONI, FOUR_CHESSES) a que esta ficha técnica se refere.
     * Utiliza o enum {@link TypePizza}.
     */
    private TypePizza productType;
    /**
     * Uma descrição textual do produto (ex: "Pizza 4 Queijos").
     */
    private String productDescription;
    /**
     * Um array de {@link MaterialNeeded} que especifica a matéria-prima e a
     * quantidade necessária para produzir **uma única unidade** deste produto.
     */
    private MaterialNeeded[] materialNeeded;

    /**
     * Construtor padrão (necessário para serialização/desserialização JSON).
     */
    public ProductTechnicalSheet() {}

    /**
     * Construtor para criar uma nova Ficha Técnica de Produto.
     *
     * @param productType O tipo de produto (pizza).
     * @param productDescription Uma descrição textual do produto.
     * @param materialNeeded Um array de {@link MaterialNeeded} detalhando os requisitos unitários.
     */
    public ProductTechnicalSheet(TypePizza productType, String productDescription, MaterialNeeded[] materialNeeded) {
        this.productType = productType;
        this.productDescription = productDescription;
        this.materialNeeded = materialNeeded;
    }

    /**
     * Obtém o tipo de produto desta ficha técnica.
     *
     * @return O tipo de pizza ({@link TypePizza}).
     */
    public TypePizza getProductType() {
        return productType;
    }

    /**
     * Obtém a descrição textual do produto.
     *
     * @return A descrição do produto.
     */
    public String getProductDescription() {
        return productDescription;
    }

    /**
     * Obtém a lista de materiais e quantidades unitárias necessárias para produzir
     * uma unidade do produto.
     *
     * @return Um array de {@link MaterialNeeded} detalhando a matéria-prima e a
     * quantidade por unidade de produto.
     */
    public MaterialNeeded[] getMaterialNeeded() {
        return materialNeeded;
    }
}
