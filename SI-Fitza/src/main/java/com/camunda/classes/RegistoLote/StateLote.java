package com.camunda.classes.RegistoLote;

import com.camunda.classes.RegistoLote.Enums.LoteState;

import java.util.ArrayList;
import java.util.List;

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
     * Objeto que contém as razões pela qual o lote foi descartado, se aplicável.
     * Este campo será {@code null} se o lote não estiver no estado {@code DISCARDED}.
     */
    private List<DiscartReason> discartReasons;
    /**
     * Construtor padrão (default constructor) exigido pela biblioteca Jackson
     * para a deserialização de objetos JSON.
     */
    public StateLote() {
        this.discartReasons = new ArrayList<>();
    }

    /**
     * Construtor para inicializar o estado do Lote.
     *
     * @param state O estado atual do lote (ex: {@code APROVED}, {@code DISCARDED}).
     */
    public StateLote(LoteState state) {
        this.discartReasons = new ArrayList<>();
        this.state = state;
    }

    /**
     * Adiciona um motivo de descarte à lista.
     * @param reason O objeto contendo o motivo e o ator responsável.
     */
    public void addDiscartReason(DiscartReason reason) {
        if (this.discartReasons == null) {
            this.discartReasons = new ArrayList<>();
        }
        this.discartReasons.add(reason);
    }

    /**
     * Obtém o objeto que descreve a razão do descarte.
     *
     * @return O objeto {@link DiscartReason}, ou {@code null} se o lote não foi descartado.
     */
    public List<DiscartReason> getDiscartReasons() {
        return discartReasons;
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
