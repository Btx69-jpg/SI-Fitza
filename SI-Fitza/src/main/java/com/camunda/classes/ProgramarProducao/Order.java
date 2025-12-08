package com.camunda.classes.ProgramarProducao;

import com.camunda.classes.Cliente;

/**
 * Representa uma **Encomenda** feita por um cliente.
 *
 * <p>Esta classe agrega informações essenciais sobre a transação, incluindo
 * o cliente, a data, o estado e a descrição detalhada dos itens encomendados.
 */
public class Order {

    /**
     * O identificador único da encomenda (ex: "ORD-12345").
     */
    private String orderId;

    /**
     * A data em que a encomenda foi realizada (geralmente em formato String ISO-8601).
     */
    private String orderDate;

    /**
     * O estado atual da encomenda (ex: "NEW", "PROCESSING", "COMPLETED", "CANCELED").
     */
    private String orderStatus;

    /**
     * O objeto {@link Cliente} que contém os dados do cliente que realizou a encomenda.
     */
    private Cliente ClientData;

    /**
     * Um array de {@link OrderDescription} que detalha todos os itens e respetivas
     * quantidades que fazem parte desta encomenda.
     */
    private OrderDescription[] orderDescription;

    /**
     * Construtor padrão (necessário para serialização/desserialização em alguns frameworks).
     */
    public Order() {}

    /**
     * Construtor para criar uma nova instância de Encomenda.
     *
     * <p>**Nota:** Este construtor inicializa o {@code orderDescription} como um array
     * contendo apenas o item {@code orderDescription} fornecido.
     *
     * @param orderId O identificador da encomenda.
     * @param orderDate A data da encomenda.
     * @param orderStatus O estado inicial da encomenda.
     * @param ClientData O objeto {@link Cliente} associado.
     * @param orderDescription O primeiro item da encomenda.
     */
    public Order(String orderId, String orderDate, String orderStatus, Cliente ClientData, OrderDescription orderDescription) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.ClientData = ClientData;
        this.orderDescription = new OrderDescription[]{orderDescription};
    }

    /**
     * Obtém o identificador da encomenda.
     * @return O ID da encomenda.
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Define o identificador da encomenda.
     * @param orderId O novo ID da encomenda.
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * Obtém a data da encomenda.
     * @return A data da encomenda.
     */
    public String getOrderDate() {
        return orderDate;
    }

    /**
     * Define a data da encomenda.
     * @param orderDate A nova data da encomenda.
     */
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Obtém o estado atual da encomenda.
     * @return O estado da encomenda.
     */
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     * Define o estado da encomenda.
     * @param orderStatus O novo estado da encomenda.
     */
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * Obtém os dados do cliente.
     * @return O objeto {@link Cliente} associado.
     */
    public Cliente getClientData() {
        return ClientData;
    }

    /**
     * Define os dados do cliente.
     * @param clientData O novo objeto {@link Cliente} associado.
     */
    public void setClientData(Cliente clientData) {
        ClientData = clientData;
    }

    /**
     * Obtém a descrição de todos os itens da encomenda.
     * @return Um array de {@link OrderDescription}.
     */
    public OrderDescription[] getOrderDescription() {
        return orderDescription;
    }

    /**
     * Adiciona um novo item à encomenda ou soma a quantidade se o item já existir.
     *
     * <p>Se o {@code TypePizza} do item fornecido já estiver presente na encomenda,
     * a quantidade é somada ao item existente. Caso contrário, o item é adicionado
     * ao array {@code orderDescription}.
     *
     * @param orderDescription O item a adicionar ou a somar.
     */
    public void addOrderDescription(OrderDescription orderDescription) {
        boolean hasOrder = false;
        for (OrderDescription description : this.orderDescription) {
            if (description.getTypePizza() == orderDescription.getTypePizza()) {
                description.setQuantity(description.getQuantity() + orderDescription.getQuantity());
                hasOrder = true;
                break;
            }
        }
        if (!hasOrder) {
            this.orderDescription = java.util.Arrays.copyOf(this.orderDescription, this.orderDescription.length + 1);
            this.orderDescription[this.orderDescription.length - 1] = orderDescription;
        }
    }

    /**
     * Altera a quantidade de um item existente na encomenda.
     *
     * <p>Se o {@code TypePizza} do item fornecido for encontrado, a quantidade
     * é substituída pela nova quantidade. Se o item não existir, ele é adicionado.
     *
     * @param orderDescription O item a ser alterado (contém o tipo e a nova quantidade).
     */
    public void changeOrderQuantity(OrderDescription orderDescription) {
        boolean hasOrder = false;
        for (OrderDescription description : this.orderDescription) {
            if (description.getTypePizza() == orderDescription.getTypePizza()) {
                description.setQuantity(orderDescription.getQuantity());
                hasOrder = true;
                break;
            }
        }
        if (!hasOrder) {
            this.orderDescription = java.util.Arrays.copyOf(this.orderDescription, this.orderDescription.length + 1);
            this.orderDescription[this.orderDescription.length - 1] = orderDescription;
        }
    }

    /**
     * Gera uma representação em String da Encomenda.
     *
     * @return Uma string contendo os dados principais da encomenda.
     */
    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", orderDate=" + orderDate + ", orderStatus=" + orderStatus + ", ClientData=" + ClientData + ", orderDescription=" + java.util.Arrays.toString(orderDescription) + "]";
    }
}