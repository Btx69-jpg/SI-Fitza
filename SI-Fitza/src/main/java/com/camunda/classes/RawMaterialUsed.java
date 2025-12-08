package com.camunda.classes;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/**
 * Classe de domínio que representa uma **Matéria-Prima utilizada** num Lote de produção,
 * registando qual o material específico, a quantidade consumida e a sua data de validade.
 * <p>
 * Esta classe é crucial para a rastreabilidade (traceability) do Lote, associando
 * os materiais consumidos (e respetiva validade) ao produto final.
 * </p>
 */
public class RawMaterialUsed {
    /**
     * O objeto {@link RawMaterial} que foi utilizado. Contém o ID, nome e o Fornecedor.
     */
    private RawMaterial rawMaterial;

    /**
     * Data de validade da matéria-prima utilizada.
     * <p>
     * É anotada com {@code @JsonFormat} para garantir que a data seja serializada/deserializada
     * no formato `dd-MM-yyyy` (dia-mês-ano), independentemente das configurações padrão do Jackson.
     * </p>
     */
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;

    /**
     * Quantidade da matéria-prima consumida neste Lote (ex: 50.5 kg).
     */
    private double quantity;

    /**
     * Construtor padrão (default constructor) exigido pela biblioteca Jackson
     * para a deserialização de objetos JSON.
     */
    public RawMaterialUsed() {}

    /**
     * Construtor para inicializar uma Matéria-Prima Utilizada.
     *
     * @param rawMaterial O material em si (ID, nome, fornecedor).
     * @param expirationDate A data de validade da matéria-prima específica usada.
     * @param quantity A quantidade consumida (em unidades de medida relevantes, ex: kg, litros).
     */
    public RawMaterialUsed(RawMaterial rawMaterial, LocalDate expirationDate, double quantity) {
        this.rawMaterial = rawMaterial;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
    }

    /**
     * Obtém os detalhes da matéria-prima utilizada.
     *
     * @return O objeto {@link RawMaterial}.
     */
    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    /**
     * Obtém a quantidade da matéria-prima utilizada.
     *
     * @return A quantidade consumida.
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Obtém a data de validade da matéria-prima utilizada.
     *
     * @return A data de validade formatada.
     */
    public LocalDate getExpirationDate() { return expirationDate; }
}
