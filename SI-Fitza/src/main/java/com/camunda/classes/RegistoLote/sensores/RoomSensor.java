package com.camunda.classes.RegistoLote.sensores;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;

/**
 * Classe base abstrata que representa um sensor ambiental (IoT) genérico na fábrica.
 * <p>
 * Esta classe normaliza os metadados comuns a qualquer dispositivo de monitorização
 * ambiental, garantindo que todos os sensores possuem uma identificação única,
 * uma localização física definida e um carimbo temporal preciso do momento da leitura.
 * </p>
 * <h3>Utilização:</h3>
 * <p>
 * É estendida por sensores específicos (ex: {@link TemperatureSensor}, {@link HumiditySensor})
 * e utilizada para agregar listas de leituras ambientais na classe {@code Lote}.
 * </p>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "sensorType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TemperatureSensor.class, name = "temperature"),
        @JsonSubTypes.Type(value = HumiditySensor.class, name = "humidity"),
        @JsonSubTypes.Type(value = AirQualitySensor.class, name = "airQuality")
})
public abstract class RoomSensor {
    /** Identificador único do dispositivo no sistema (ex: "SENS-TEMP-01"). */
    private String sensorId;

    /**
     * Localização física onde o sensor está instalado.
     * Ex: "Zona de Fermentação", "Armazém de Farinha", "Linha de Embalamento".
     */
    private String location;

    /**
     * Data e hora exata em que a leitura foi capturada pelo sensor.
     * É definida automaticamente no momento da instanciação do objeto.
     */
    private LocalDateTime readAt;

    public RoomSensor() {}
    /**
     * Construtor base para inicializar um sensor ambiental.
     * <p>
     * O atributo {@code readAt} é preenchido automaticamente com a hora atual do sistema
     * ({@link LocalDateTime#now()}), assumindo que o objeto é criado no momento da leitura (tempo real).
     * </p>
     *
     * @param sensorId O ID único do sensor.
     * @param location A zona da fábrica onde o sensor se encontra.
     */
    public RoomSensor(String sensorId, String location) {
        this.sensorId = sensorId;
        this.location = location;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Obtém o identificador único do sensor.
     * @return String com o ID do sensor.
     */
    public String getSensorId() { return sensorId; }

    /**
     * Obtém a localização física do sensor na fábrica.
     * @return String com o nome da zona ou sala.
     */
    public String getLocation() { return location; }

    /**
     * Obtém o carimbo temporal da leitura.
     * @return {@link LocalDateTime} correspondente ao momento da criação do registo.
     */
    public LocalDateTime getReadAt() { return readAt; }
}
