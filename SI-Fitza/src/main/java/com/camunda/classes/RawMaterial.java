package com.camunda.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe de domínio que representa uma **Matéria-Prima** básica no sistema,
 * fornecendo detalhes sobre o material e o seu fornecedor.
 * <p>
 * Esta classe é o componente base que, juntamente com a quantidade e validade,
 * é usado na classe {@link RawMaterialUsed} para registar o consumo no Lote.
 * </p>
 */
public class RawMaterial {
    /**
     * Identificador único da matéria-prima (ex: SKU, código interno).
     */
    private String rawMaterialId;

    /**
     * Nome comercial ou descritivo da matéria-prima.
     * <p>
     * Utiliza a anotação {@code @JsonProperty("materialName")} do Jackson
     * para garantir que o campo JSON gerado é {@code "materialName"},
     * mesmo que a variável interna seja {@code name}.
     * </p>
     */
    @JsonProperty("materialName")
    private String name;

    /**
     * O fornecedor associado a esta matéria-prima.
     */
    private Supplier supplier;

    /**
     * Construtor padrão (default constructor) exigido pela biblioteca Jackson
     * para a deserialização de objetos JSON.
     */
    public RawMaterial() {}

    /**
     * Construtor para inicializar uma nova Matéria-Prima.
     *
     * @param rawMaterialId O ID único da matéria-prima.
     * @param name O nome da matéria-prima.
     * @param supplier O objeto {@link Supplier} associado a esta matéria-prima.
     */
    public RawMaterial(String rawMaterialId, String name, Supplier supplier) {
        this.rawMaterialId = rawMaterialId;
        this.name = name;
        this.supplier = supplier;
    }

    /**
     * Obtém o identificador único da matéria-prima.
     *
     * @return O ID da matéria-prima.
     */
    public String getRawMaterialId() {
        return rawMaterialId;
    }

    /**
     * Obtém o nome da matéria-prima (serializado como {@code materialName}).
     *
     * @return O nome da matéria-prima.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome da matéria-prima.
     *
     * @param name O novo nome da matéria-prima.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtém o objeto fornecedor associado.
     *
     * @return O objeto {@link Supplier}.
     */
    public Supplier getSupplier() {
        return supplier;
    }

    /**
     * Define o fornecedor associado a esta matéria-prima.
     *
     * @param supplier O novo objeto {@link Supplier}.
     */
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
