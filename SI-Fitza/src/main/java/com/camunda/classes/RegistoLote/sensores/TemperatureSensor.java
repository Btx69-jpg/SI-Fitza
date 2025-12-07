package com.camunda.classes.RegistoLote.sensores;

/**
 * Representa um Sensor de Temperatura Ambiente.
 * <p>
 * Este sensor é crítico para o controlo do processo de fermentação da massa.
 * A temperatura ambiente influencia diretamente a atividade das leveduras:
 * <ul>
 * <li><b>Muito alta:</b> Fermentação excessiva, massa azeda ou sem estrutura.</li>
 * <li><b>Muito baixa:</b> Massa não cresce, produto final denso e pesado.</li>
 * </ul>
 * </p>
 */
public class TemperatureSensor extends RoomSensor {
    /** Temperatura medida em graus Celsius (°C). */
    private double temperatureCelsius;

    public TemperatureSensor() {
        super();
    }
    /**
     * Constrói uma nova leitura de temperatura.
     *
     * @param sensorId ID único do sensor.
     * @param location Localização na fábrica.
     * @param temperatureCelsius Valor da temperatura lida em °C.
     */
    public TemperatureSensor(String sensorId, String location, double temperatureCelsius) {
        super(sensorId, location);
        this.temperatureCelsius = temperatureCelsius;
    }

    /**
     * Obtém o valor da temperatura ambiente registada.
     *
     * @return Temperatura em graus Celsius (°C).
     */
    public double getTemperatureCelsius() {
        return temperatureCelsius;
    }
}
