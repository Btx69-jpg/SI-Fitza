package com.camunda.classes.ProgramarProducao;

import com.camunda.classes.RawMaterial;

/**
 * Representa uma **Necessidade de Material** específica, que liga uma
 * {@link RawMaterial} (matéria-prima) à quantidade necessária.
 *
 * <p>Esta classe é utilizada para definir os requisitos de matéria-prima numa
 * {@code ProductTechnicalSheet} (ficha técnica - quantidade unitária) ou para
 * armazenar a quantidade total necessária para uma encomenda inteira.
 */
public class MaterialNeeded {
    /**
     * O objeto {@link RawMaterial} que representa a matéria-prima necessária
     * (ex: Farinha, Queijo).
     */
    private RawMaterial rawMaterial;
    /**
     * A quantidade da matéria-prima necessária.
     * Esta quantidade pode ser a unidade (na ficha técnica) ou o total (na lista de necessidades da encomenda).
     */
    private int quantity;

    /**
     * Construtor padrão (necessário para serialização/desserialização em alguns frameworks).
     */
    public MaterialNeeded() {}

    /**
     * Construtor para criar uma nova instância de Necessidade de Material.
     *
     * @param rawMaterial O objeto {@link RawMaterial} necessário.
     * @param quantity A quantidade desta matéria-prima (inteiro).
     */
    public MaterialNeeded(RawMaterial rawMaterial, int quantity) {
        this.rawMaterial = rawMaterial;
        this.quantity = quantity;
    }

    /**
     * Obtém o objeto {@link RawMaterial} associado a esta necessidade.
     *
     * @return A matéria-prima.
     */
    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    /**
     * Obtém a quantidade desta matéria-prima necessária.
     *
     * @return A quantidade.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Define ou atualiza a quantidade desta matéria-prima necessária.
     *
     * @param quantity A nova quantidade.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Define ou atualiza o objeto {@link RawMaterial} associado.
     *
     * @param rawMaterial A nova matéria-prima.
     */
    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }
}
