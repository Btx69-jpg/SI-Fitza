package com.camunda.handles.RegistoLote;

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

/**
 * Worker do Camunda/Zeebe que **simula a leitura de dados de sensores ambientais** (IoT).
 * <p>
 * O Worker cria instâncias de subclasses de {@link RoomSensor} (como {@link TemperatureSensor}, {@link HumiditySensor}, e {@link AirQualitySensor}),
 * preenche-as com dados simulados e, em seguida, serializa a lista para o contexto do processo Zeebe.
 * </p>
 * <p>
 * A lista de sensores é enviada para o processo na variável temporária **{@code temp_sensors_list}**,
 * aguardando a consolidação no objeto Lote principal por um *Worker* de *Merge* subsequente.
 * </p>
 *
 * <h3>Serialização (Polimorfismo):</h3>
 * O código realiza uma verificação para garantir que o Jackson está a incluir o campo discriminador (`sensorType`)
 * para que a deserialização polimórfica seja bem-sucedida ao ser lida por Workers posteriores.
 */
public class RecordAmbientSensorDataHandle implements JobHandler {

    private final Random random = new Random();

    /**
     * Lógica de tratamento para o Job. Simula a leitura dos dados dos sensores ambientais,
     * serializa-os e envia-os para o contexto do processo.
     *
     * @param client O cliente do Job, usado para completar ou falhar o Job.
     * @param job O Job ativado pelo motor Zeebe.
     * @throws Exception Se ocorrer um erro durante a simulação ou serialização dos dados.
     */
    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        System.out.println("\n>>> [IOT SENSORS] A ler sensores ambientais da sala de produção...");

        try {
            //Simulação do Sensor de Temperatura (20°C a 26°C)
            double currentTemp = 20 + (random.nextDouble() * 6);
            TemperatureSensor tempSensor = new TemperatureSensor(
                    "SENS-TEMP-01",
                    "Sala de Fermentação A",
                    currentTemp
            );

            //Simulação do Sensor de Humidade (50% a 70%)
            double currentHum = 50 + (random.nextDouble() * 20);
            HumiditySensor humSensor = new HumiditySensor(
                    "SENS-HUM-02",
                    "Sala de Fermentação A",
                    currentHum
            );

            //Simulação do Sensor de Qualidade do Ar (10 a 50 ppm/unidades)
            double currentAirQuality = 10 + (random.nextDouble() * 40);
            AirQualitySensor airSensor = new AirQualitySensor(
                    "SENS-AIR-01",
                    "Geral Fábrica",
                    currentAirQuality
            );

            //Agrupar todos os sensores numa lista polimórfica
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

            //Converter para List<Map<String, Object>> para o formato do Zeebe
            List<Map<String, Object>> serializedSensors = LoteUtils.getMapper().readValue(
                    jsonString,
                    new TypeReference<>() {}
            );

            //Enviar para o processo na variável temporária
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
