package com.camunda.classes.RegistoLote.Enums;

/**
 * Enum que define os **Tipos de Pizza** que podem ser produzidos e rastreados
 * no sistema de gestão de Lotes.
 * <p>
 * Este Enum é utilizado na classe {@code Lote} para identificar de forma padronizada
 * o produto fabricado, essencial para a rastreabilidade e relatórios de produção.
 * </p>
 */
public enum TypePizza {
    /**
     * Pizza de Quatro Queijos.
     */
    FOUR_CHESSES,

    /**
     * Pizza Vegetariana.
     */
    VEGETARIAN,

    /**
     * Pizza de Queijo e Enchidos (Frios).
     */
    CHEESE_COLD_CUTS,

    /**
     * Pizza de Pepperoni.
     */
    PEPPERONI
}