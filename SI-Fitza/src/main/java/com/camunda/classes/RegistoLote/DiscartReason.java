package com.camunda.classes.RegistoLote;

import com.camunda.classes.RegistoLote.Enums.ActorDiscartLote;

/**
 * Classe de domínio que encapsula o **motivo específico e o ator responsável**
 * pelo descarte ou rejeição de um Lote de Produção.
 * <p>
 * Esta classe é utilizada dentro de {@link StateLote} quando o estado do Lote
 * é definido como {@code DISCARDED}. Ajuda a fornecer rastreabilidade sobre
 * quem (ator) e por que (razão) o lote foi rejeitado.
 * </p>
 */
public class DiscartReason {
    /**
     * O ator ou departamento que iniciou a ação de descarte, definido pelo Enum {@link ActorDiscartLote}.
     */
    private ActorDiscartLote actor;

    /**
     * Descrição detalhada do motivo ou justificação do descarte (ex: "Contaminação por fermento", "Temperatura incorreta").
     */
    private String reason;

    /**
     * Construtor padrão (default constructor) exigido pela biblioteca Jackson
     * para a deserialização de objetos JSON.
     */
    public DiscartReason() {}

    /**
     * Construtor para inicializar o motivo do descarte.
     *
     * @param reason A descrição textual detalhada do motivo.
     * @param actor O ator responsável pelo descarte (ex: LABORATORY, PRODUCTION).
     */
    public DiscartReason(String reason, ActorDiscartLote actor) {
        this.reason = reason;
        this.actor = actor;
    }

    /**
     * Obtém a descrição detalhada do motivo do descarte.
     *
     * @return A razão textual.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Obtém o ator responsável pelo descarte.
     *
     * @return O {@link ActorDiscartLote} (Enum).
     */
    public ActorDiscartLote getActor() {
        return actor;
    }
}
