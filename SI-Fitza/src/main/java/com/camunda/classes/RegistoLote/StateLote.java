package com.camunda.classes.RegistoLote;

import com.camunda.classes.RegistoLote.Enums.LoteState;

/**
 * Classe de domínio que representa o **Estado atual do Lote** no ciclo de vida
 * da produção.
 * <p>
 * Esta classe encapsula o {@link LoteState} principal (ex: Em Produção, Aprovado, Descartado)
 * e, opcionalmente, o motivo do descarte (se o estado for {@code LoteState.DISCARDED}).
 * </p>
 * <p>
 * A utilização desta classe garante que a razão de descarte está sempre ligada
 * de forma semântica ao estado do lote.
 * </p>
 */
public class StateLote {
    /**
     * O estado principal do lote, definido pelo Enum {@link LoteState}.
     */
    private LoteState state;

    /**
     * Objeto que contém a razão pela qual o lote foi descartado, se aplicável.
     * Este campo será {@code null} se o lote não estiver no estado {@code DISCARDED}.
     */
    private DiscartReason discartReason;

    /**
     * Construtor padrão (default constructor) exigido pela biblioteca Jackson
     * para a deserialização de objetos JSON.
     */
    public StateLote() { }

    /**
     * Construtor para inicializar o estado do Lote.
     *
     * @param discartReason O motivo do descarte. Deve ser {@code null} se o estado não for {@code DISCARDED}.
     * @param state O estado atual do lote (ex: {@code APROVED}, {@code DISCARDED}).
     */
    public StateLote(DiscartReason discartReason, LoteState state) {
        this.discartReason = discartReason;
        this.state = state;
    }

    /**
     * Obtém o objeto que descreve a razão do descarte.
     *
     * @return O objeto {@link DiscartReason}, ou {@code null} se o lote não foi descartado.
     */
    public DiscartReason getDiscartReason() {
        return discartReason;
    }

    /**
     * Obtém o estado atual do lote.
     *
     * @return O {@link LoteState} (Enum).
     */
    public LoteState getState() {
        return state;
    }
}
