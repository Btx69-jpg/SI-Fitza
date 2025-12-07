package com.camunda.classes.RegistoLote.sensores;

/**
 * Representa um Sensor de Qualidade do Ar (Monitorização de Partículas).
 * <p>
 * Numa fábrica de massas e pizzas, o pó de farinha em suspensão (PM2.5 / PM10) é um risco duplo:
 * <ol>
 * <li><b>Saúde Ocupacional:</b> A inalação excessiva prejudica os operadores ("Asma de Padeiro").</li>
 * <li><b>Segurança (ATEX):</b> Concentrações muito elevadas de pó orgânico podem ser explosivas.</li>
 * </ol>
 * Este sensor monitoriza esses níveis para garantir que a ventilação está a funcionar corretamente.
 * </p>
 */
public class AirQualitySensor extends RoomSensor {
    /**
     * Nível de Matéria Particulada (PM2.5) medido em microgramas por metro cúbico (µg/m³).
     * <p>
     * Valores acima de 25-30 µg/m³ geralmente indicam má qualidade do ar interior
     * e necessidade de aumentar a exaustão.
     * </p>
     */
    private double particulateMatterLevel;

    public AirQualitySensor() {
        super();
    }
    /**
     * Constrói uma nova leitura de qualidade do ar.
     *
     * @param sensorId              ID único do sensor (ex: "SENS-AIR-01").
     * @param location              Localização na fábrica.
     * @param particulateMatterLevel Concentração de partículas (µg/m³).
     */
    public AirQualitySensor(String sensorId, String location, double particulateMatterLevel) {
        super(sensorId, location);
        this.particulateMatterLevel = particulateMatterLevel;
    }

    /**
     * Obtém o nível de partículas registado.
     *
     * @return Valor em µg/m³.
     */
    public double getParticulateMatterLevel() {
        return particulateMatterLevel;
    }
}
