package com.camunda.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe de domínio que representa um **Fornecedor** de matérias-primas
 * ou outros bens essenciais ao processo produtivo.
 * <p>
 * Esta classe é utilizada para associar matérias-primas ao seu fornecedor
 * no contexto do objeto {@code Lote}.
 * </p>
 */
public class Supplier {
    /**
     * Identificador único do fornecedor no sistema de gestão (ex: ID no ERP).
     */
    public String supplierId;

    /**
     * Nome do fornecedor.
     * <p>
     * Utiliza a anotação {@code @JsonProperty("supplierName")} do Jackson
     * para garantir que o campo JSON gerado é {@code "supplierName"},
     * mesmo que a variável interna seja {@code name}.
     * </p>
     */
    @JsonProperty("supplierName")
    public String name;

    /**
     * Construtor padrão (default constructor) exigido pela biblioteca Jackson
     * para a deserialização de objetos JSON.
     */
    public Supplier() {}

    /**
     * Construtor para inicializar um novo Fornecedor.
     *
     * @param supplierId O ID único do fornecedor.
     * @param name O nome comercial do fornecedor.
     */
    public Supplier(String supplierId, String name) {
        this.supplierId = supplierId;
        this.name = name;
    }

    /**
     * Obtém o identificador único do fornecedor.
     *
     * @return O ID do fornecedor.
     */
    public String getSupplierId() {
        return supplierId;
    }

    /**
     * Obtém o nome do fornecedor (que será serializado como {@code supplierName}).
     *
     * @return O nome do fornecedor.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome do fornecedor.
     *
     * @param name O novo nome comercial do fornecedor.
     */
    public void setName(String name) {
        this.name = name;
    }
}
