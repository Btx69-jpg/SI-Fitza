package com.camunda.classes.RegistoLote.Enums;

/**
 * Enum que define os **Estados Críticos** pelos quais um Lote de Produção
 * pode passar durante ou após o seu ciclo de vida.
 * <p>
 * Este estado é fundamental para determinar se o produto final é seguro e
 * apto para distribuição.
 * </p>
 */
public enum LoteState {
    /**
     * O Lote está **Bloqueado**. Este é tipicamente o estado inicial após a criação
     * ou um estado de espera enquanto se aguardam validações (ex: resultados laboratoriais).
     * O produto não pode ser expedido.
     */
    BLOCKED,

    /**
     * O Lote foi **Descartado/Rejeitado**. O produto falhou nos testes de qualidade
     * e deve ser destruído ou reprocessado (se aplicável).
     */
    DISCARDED,

    /**
     * O Lote foi **Aprovado/Libertado**. O produto passou em todos os testes de qualidade
     * e está apto para expedição e consumo.
     */
    APROVED
}