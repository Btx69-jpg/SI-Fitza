package com.camunda.classes.RegistoLote.Enums;

/**
 * Enum que define os **Tipos de Procedimentos de Limpeza** que podem ser registados
 * na linha de produção antes ou durante o ciclo de vida de um {@code Lote}.
 * <p>
 * Este Enum é utilizado na classe {@code CleaningLine}.
 * </p>
 */
public enum TypeCleaning {
    /**
     * Limpeza padrão realizada entre o final de um Lote de produção e o início do próximo.
     * Geralmente é um procedimento mais rápido focado na remoção de resíduos imediatos.
     */
    END_OF_BATCH,

    /**
     * Limpeza profunda ou sanitização completa da linha de produção.
     * Realizada em intervalos programados ou após problemas de contaminação.
     */
    DEEP_CLEANING
}