package com.camunda.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe de domínio que representa um **Cliente** associado a um Lote
 * de produção, tipicamente quando a produção é feita sob encomenda (ordem).
 * <p>
 * Esta classe armazena informações básicas do cliente para rastreabilidade e
 * futuras comunicações.
 * </p>
 */
public class Cliente {

    /**
     * Identificador único do cliente no sistema (ex: ID no CRM/ERP).
     */
    private String clienteId;

    /**
     * Nome do cliente.
     * <p>
     * É anotado com {@code @JsonProperty("clienteName")} para garantir que
     * o nome da propriedade JSON seja {@code "clienteName"}, mesmo que o nome
     * da variável interna seja {@code name}.
     * </p>
     */
    @JsonProperty("clienteName")
    private String name;

    /**
     * Endereço de email do cliente.
     */
    private String mail;

    /**
     * Construtor padrão (default constructor) exigido pela biblioteca Jackson
     * para a deserialização de objetos JSON.
     */
    public Cliente() {}

    /**
     * Construtor para inicializar um novo Cliente apenas com ID e Nome.
     * O email é opcional e pode ser definido posteriormente.
     *
     * @param clienteId O ID único do cliente.
     * @param name O nome do cliente.
     */
    public Cliente(String clienteId, String name) {
        this.clienteId = clienteId;
        this.name = name;
    }

    /**
     * Obtém o identificador único do cliente.
     *
     * @return O ID do cliente.
     */
    public String getClienteId() {
        return clienteId;
    }

    /**
     * Obtém o nome do cliente (serializado como {@code clienteName}).
     *
     * @return O nome do cliente.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome do cliente.
     *
     * @param name O novo nome do cliente.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtém o endereço de email do cliente.
     *
     * @return O email do cliente.
     */
    public String getMail() {
        return mail;
    }

    /**
     * Define o endereço de email do cliente.
     *
     * @param mail O novo endereço de email.
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

}