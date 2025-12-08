package com.camunda.classes.RegistoLote.Enums;

/**
 * Enum que lista as diferentes **Linhas de Produção** existentes na fábrica
 * que podem ser associadas a um {@code Lote} ou a um registo de limpeza.
 */
public enum ProductionLine {
    /**
     * Linha dedicada à preparação da massa base das pizzas (mistura, fermentação, corte).
     */
    PIZZA_DOUGH_LINE,

    /**
     * Linha dedicada ao processamento e aplicação do molho nas bases de pizza.
     */
    PIZZA_SAUCE_LINE
}