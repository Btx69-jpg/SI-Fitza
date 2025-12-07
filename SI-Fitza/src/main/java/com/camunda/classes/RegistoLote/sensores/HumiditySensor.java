package com.camunda.classes.RegistoLote.sensores;

/**
 * Representa um Sensor de Humidade Relativa do Ar.
 * <p>
 * A monitorização da humidade é essencial para manter a qualidade da superfície da massa.
 * <ul>
 * <li><b>Humidade Baixa (<50%):</b> Risco de formação de "crosta" seca na massa (skinning),
 * o que impede o crescimento correto no forno.</li>
 * <li><b>Humidade Alta (>85%):</b> A massa torna-se pegajosa e difícil de manusear pelas máquinas.</li>
 * </ul>
 * </p>
 */
public class HumiditySensor extends RoomSensor {

    /** Percentagem de humidade relativa do ar (0.0 a 100.0%). */
    private double humidityPercentage;

    public HumiditySensor() {
        super();
    }
    /**
     * Constrói uma nova leitura de humidade.
     *
     * @param sensorId ID único do sensor.
     * @param location Localização na fábrica.
     * @param humidityPercentage Valor da humidade relativa em percentagem.
     */
    public HumiditySensor(String sensorId, String location, double humidityPercentage) {
        super(sensorId, location);
        this.humidityPercentage = humidityPercentage;
    }

    /**
     * Obtém a percentagem de humidade relativa registada.
     *
     * @return Valor double representando a percentagem (ex: 65.5).
     */
    public double getHumidityPercentage() {
        return humidityPercentage;
    }
}