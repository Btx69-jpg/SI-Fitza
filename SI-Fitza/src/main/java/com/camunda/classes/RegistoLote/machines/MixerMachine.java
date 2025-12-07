package com.camunda.classes.RegistoLote.machines;

import com.camunda.classes.RegistoLote.Enums.MachineStatus;

/**
 * Representa uma Misturadora Industrial (Mixer) na linha de produção.
 * <p>
 * Esta máquina é responsável pela primeira etapa de produção: a mistura dos ingredientes
 * para a criação da massa da pizza. Monitoriza parâmetros críticos como a temperatura da massa
 * (vital para a fermentação) e a carga do motor (indicação de viscosidade).
 * </p>
 * * <h3>Utilização:</h3>
 * <p>
 * Instanciada no worker {@code UpdateMaterialLoteHandle} para simular ou registar dados
 * da preparação do lote. Os dados recolhidos aqui podem determinar se um lote de massa
 * é aprovado ou rejeitado por excesso de calor ou falta de homogeneidade.
 * </p>
 */
public class MixerMachine extends Machine {
    /** Rotações por minuto do braço misturador. */
    private double rpm;

    /** Temperatura atual da massa em graus Celsius. Crítico para evitar fermentação precoce. */
    private double doughTemp;

    /** Corrente elétrica do motor em Amperes. Usado para inferir a densidade/viscosidade da massa. */
    private double motorAmps;

    public MixerMachine() {
        super();
    }

    /**
     * Constrói uma nova instância de Misturadora com dados de telemetria.
     *
     * @param machineId   ID da máquina.
     * @param machineName Nome da máquina.
     * @param status      Estado operacional.
     * @param rpm         Velocidade de rotação atual.
     * @param doughTemp   Temperatura da massa (°C).
     * @param motorAmps   Consumo do motor (A).
     */
    public MixerMachine(String machineId, String machineName, MachineStatus status, double rpm, double doughTemp, double motorAmps) {
        super(machineId, machineName, status);
        this.rpm = rpm;
        this.doughTemp = doughTemp;
        this.motorAmps = motorAmps;
    }

    /**
     * Obtém as Rotações Por Minuto atuais.
     * @return valor double do RPM.
     */
    public double getRpm() { return rpm; }

    /**
     * Obtém a temperatura da massa.
     * @return temperatura em graus Celsius.
     */
    public double getDoughTemp() { return doughTemp; }

    /**
     * Obtém a leitura da corrente elétrica do motor.
     * <p>
     * Um valor acima do normal pode indicar que a massa está demasiado densa/dura
     * ou que existe uma obstrução mecânica.
     * </p>
     * @return Corrente em Amperes (A).
     */
    public double getMotorAmps() { return motorAmps; }
}
