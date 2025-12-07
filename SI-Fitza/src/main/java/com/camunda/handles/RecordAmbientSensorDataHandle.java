package com.camunda.handles;

import com.camunda.classes.RegistoLote.Lote;
import com.camunda.classes.RegistoLote.sensores.AirQualitySensor;
import com.camunda.classes.RegistoLote.sensores.HumiditySensor;
import com.camunda.classes.RegistoLote.sensores.RoomSensor;
import com.camunda.classes.RegistoLote.sensores.TemperatureSensor;
import com.camunda.utils.LoteUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.*;

public class RecordAmbientSensorDataHandle implements JobHandler {

    private final Random random = new Random();

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [IOT SENSORS] A ler sensores ambientais da sala de produção...");

        try {
            double currentTemp = 20 + (random.nextDouble() * 6);
            TemperatureSensor tempSensor = new TemperatureSensor(
                    "SENS-TEMP-01",
                    "Sala de Fermentação A",
                    currentTemp
            );

            double currentHum = 50 + (random.nextDouble() * 20);
            HumiditySensor humSensor = new HumiditySensor(
                    "SENS-HUM-02",
                    "Sala de Fermentação A",
                    currentHum
            );

            double currentAirQuality = 10 + (random.nextDouble() * 40);
            AirQualitySensor airSensor = new AirQualitySensor(
                    "SENS-AIR-01",
                    "Geral Fábrica",
                    currentAirQuality
            );

            List<RoomSensor> collectedSensors = new ArrayList<>();
            collectedSensors.add(tempSensor);
            collectedSensors.add(humSensor);
            collectedSensors.add(airSensor);

            System.out.println(">>> [IOT] 3 Sensores lidos. A enviar para merge.");

            String jsonString = LoteUtils.getMapper()
                    .writerFor(new TypeReference<List<RoomSensor>>() {})
                    .writeValueAsString(collectedSensors);

            System.out.println(">>> DEBUG JSON SENSORES: " + jsonString);

            if (!jsonString.contains("sensorType")) {
                throw new RuntimeException("ERRO: O JSON dos sensores não tem 'sensorType'.");
            }

            List<Map<String, Object>> serializedSensors = LoteUtils.getMapper().readValue(
                    jsonString,
                    new TypeReference<>() {}
            );

            Map<String, Object> outputVariables = new HashMap<>();
            outputVariables.put("temp_sensors_list", serializedSensors);

            client.newCompleteCommand(job.getKey())
                    .variables(outputVariables)
                    .send()
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Erro ao registar dados das máquinas: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}
