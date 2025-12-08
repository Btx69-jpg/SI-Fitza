package com.camunda.classes.ProgramarProducao;

import com.camunda.classes.Cliente;

public class Order {
    private String orderId;
    private String orderDate;
    private String orderStatus;
    private Cliente ClientData;
    private OrderDescription[] orderDescription;

    public Order() {}

    public Order(String orderId, String orderDate, String orderStatus, Cliente ClientData, OrderDescription orderDescription) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.ClientData = ClientData;
        this.orderDescription = new OrderDescription[]{orderDescription};
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public Cliente getClientData() {
        return ClientData;
    }
    public void setClientData(Cliente clientData) {
        ClientData = clientData;
    }
    public OrderDescription[] getOrderDescription() {
        return orderDescription;
    }
    public void addOrderDescription(OrderDescription orderDescription) {
        boolean hasOrder = false;
        for (OrderDescription description : this.orderDescription) {
            if (description.getTypePizza() == orderDescription.getTypePizza()) {
                description.setQuantity(description.getQuantity() + orderDescription.getQuantity());
                hasOrder = true;
            }
        }
        if (!hasOrder) {
            this.orderDescription = java.util.Arrays.copyOf(this.orderDescription, this.orderDescription.length + 1);
            this.orderDescription[this.orderDescription.length - 1] = orderDescription;
        }
    }

    public void changeOrderQuantity(OrderDescription orderDescription) {
        boolean hasOrder = false;
        for (OrderDescription description : this.orderDescription) {
            if (description.getTypePizza() == orderDescription.getTypePizza()) {
                description.setQuantity(orderDescription.getQuantity());
                hasOrder = true;
            }
        }
        if (!hasOrder) {
            this.orderDescription = java.util.Arrays.copyOf(this.orderDescription, this.orderDescription.length + 1);
            this.orderDescription[this.orderDescription.length - 1] = orderDescription;
        }
    }

    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", orderDate=" + orderDate + ", orderStatus=" + orderStatus + ", ClientData=" + ClientData + ", orderDescription=" + orderDescription + "]";
    }
}
