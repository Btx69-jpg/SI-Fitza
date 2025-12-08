package com.camunda.classes.ProgramarProducao;

import com.camunda.classes.RegistoLote.Enums.TypePizza;

/**
 * Representa um **Item de Encomenda** (Order Description), detalhando um tipo
 * de produto específico e a respetiva quantidade encomendada.
 *
 * <p>Esta classe é o componente de linha de item de uma {@link Order} e é crucial
 * para saber o que e quanto deve ser produzido.
 */
public class OrderDescription {
    /**
     * O tipo de pizza (produto) que está a ser encomendado.
     * Utiliza o enum {@link TypePizza}.
     */
    private TypePizza typePizza;
    /**
     * A quantidade deste tipo de pizza que foi encomendada.
     */
    private int quantity;

    /**
     * Construtor padrão (necessário para serialização/desserialização em alguns frameworks).
     */
    public OrderDescription() {}

    /**
     * Construtor para criar uma nova descrição de item de encomenda.
     *
     * @param typePizza O tipo de pizza a ser encomendado.
     * @param quantity A quantidade do tipo de pizza.
     */
    public OrderDescription(TypePizza typePizza, int quantity) {
        this.typePizza = typePizza;
        this.quantity = quantity;
    }

    /**
     * Obtém o tipo de pizza do item de encomenda.
     *
     * @return O {@link TypePizza} do item.
     */
    public TypePizza getTypePizza() {
        return typePizza;
    }

    /**
     * Obtém a quantidade encomendada deste item.
     *
     * @return A quantidade.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Define ou atualiza a quantidade encomendada deste item.
     *
     * @param quantity A nova quantidade.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Define ou atualiza o tipo de pizza deste item.
     *
     * @param typePizza O novo {@link TypePizza}.
     */
    public void setTypePizza(TypePizza typePizza) {
        this.typePizza = typePizza;
    }
}
