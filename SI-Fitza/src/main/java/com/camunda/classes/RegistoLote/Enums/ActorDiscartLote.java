package com.camunda.classes.RegistoLote.Enums;

/**
 * Enum que identifica os **Atores ou Departamentos** que têm autoridade
 * para rejeitar ou descartar um Lote de Produção.
 * <p>
 * Este Enum é utilizado na classe {@code DiscartReason} para indicar a origem
 * da decisão de rejeição, sendo vital para a auditoria e rastreabilidade.
 * </p>
 */
public enum ActorDiscartLote {
    /**
     * O Laboratório/Análise Química ou Microbiológica determinou a rejeição do Lote
     * com base em resultados de testes.
     */
    LABORATORY,

    /**
     * O Controlo de Qualidade (QC) determinou a rejeição do Lote com base em
     * inspeções visuais ou físicas diretas no chão de fábrica.
     */
    QUALITY_CONTROL
}