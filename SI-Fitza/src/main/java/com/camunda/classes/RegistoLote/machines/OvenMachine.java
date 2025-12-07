package com.camunda.classes.RegistoLote.machines;

import com.camunda.classes.RegistoLote.Enums.MachineStatus;

/**
 * Representa um Forno de Túnel Contínuo (Tunnel Oven) na linha de produção.
 * <p>
 * O forno é dividido em zonas de aquecimento independentes para garantir a cozedura
 * uniforme da base da pizza. Controla também a velocidade do tapete rolante.
 * </p>
 * * <h3>Utilização:</h3>
 * <p>
 * Utilizada na etapa final de processamento do lote. O método {@link #getAvgTemp()} é frequentemente
 * usado em regras de negócio (DMN ou Gateways no Camunda) para validar se a pizza foi cozida
 * à temperatura correta, garantindo a segurança alimentar e qualidade.
 * </p>
 */
public class OvenMachine extends Machine {
    /** Temperatura na zona de entrada do forno (°C). */
    private double temperatureZone1;

    /** Temperatura na zona central do forno (°C). */
    private double temperatureZone2;

    /** Velocidade de deslocação do tapete rolante em cm/min. */
    private double beltSpeed;

    public OvenMachine() {
        super();
    }
    /**
     * Constrói uma nova instância do Forno com leituras das zonas de calor.
     *
     * @param id     ID da máquina.
     * @param name   Nome da máquina.
     * @param status Estado operacional.
     * @param t1     Temperatura da Zona 1 (Entrada).
     * @param t2     Temperatura da Zona 2 (Centro).
     * @param speed  Velocidade do tapete.
     */
    public OvenMachine(String id, String name, MachineStatus status, double t1, double t2, double speed) {
        super(id, name, status);
        this.temperatureZone1 = t1;
        this.temperatureZone2 = t2;
        this.beltSpeed = speed;
    }

    /**
     * Calcula a temperatura média global do forno baseada nas zonas ativas.
     * <p>
     * Este valor é geralmente usado como métrica de qualidade ("Quality Gate")
     * para aprovação do lote.
     * </p>
     *
     * @return A média aritmética das temperaturas das zonas 1 e 2.
     */
    public double getAvgTemp() { return (temperatureZone1 + temperatureZone2) / 2; }

    /**
     * Obtém a temperatura da zona de entrada (Zona 1).
     * <p>
     * Esta zona é responsável pelo choque térmico inicial na massa.
     * </p>
     * @return Temperatura em graus Celsius.
     */
    public double getTemperatureZone1() { return temperatureZone1; }

    /**
     * Obtém a velocidade atual do tapete rolante.
     * <p>
     * A velocidade determina o tempo de permanência ("dwell time") do produto dentro do forno.
     * </p>
     * @return Velocidade em cm/min.
     */
    public double getBeltSpeed() { return beltSpeed; }

    /**
     * Obtém a temperatura da zona central (Zona 2).
     * <p>
     * É nesta zona que ocorre a estabilização do cozimento do recheio.
     * </p>
     * @return Temperatura em graus Celsius.
     */
    public double getTemperatureZone2() { return temperatureZone2; }
}
