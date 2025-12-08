package com.camunda.classes.RegistoLote.Enums;

/**
 * Enum que define os possíveis **Estados Operacionais** de uma máquina
 * ({@code Machine}) no chão de fábrica, conforme registado pelo sistema MES/SCADA.
 * <p>
 * É usado na telemetria de máquinas para fins de monitorização e manutenção.
 * </p>
 */
public enum MachineStatus {
    /**
     * A máquina está a funcionar e a executar a sua tarefa de produção.
     */
    RUNNING,

    /**
     * A máquina está ligada e pronta, mas não está a executar uma tarefa (ociosa).
     */
    IDLE,

    /**
     * A máquina está desligada ou parou a sua operação de forma controlada.
     */
    STOPPED,

    /**
     * A máquina encontrou um problema técnico ou falha (exige intervenção).
     */
    ERROR,

    /**
     * A máquina está parada para manutenção programada ou corretiva.
     */
    MAINTENANCE
}