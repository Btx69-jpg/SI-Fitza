package com.camunda.classes.RegistoLote.machines;

import com.camunda.classes.RegistoLote.Enums.MachineStatus;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;

/**
 * Classe base abstrata que representa uma máquina genérica na linha de produção da fábrica.
 * <p>
 * Esta classe normaliza os atributos comuns a qualquer equipamento industrial (ID, Nome, Estado e Timestamp),
 * permitindo o tratamento polimórfico de diferentes tipos de máquinas (como Fornos ou Misturadoras).
 * </p>
 * * <h3>Utilização:</h3>
 * <p>
 * É utilizada principalmente pelos <b>JobWorkers</b> (como o {@code UpdateMaterialLoteHandle}) para instanciar
 * dados de telemetria recebidos do sistema MES antes de serem processados ou enviados para o Camunda.
 * </p>
 *
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "machineType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MixerMachine.class, name = "mixer"),
        @JsonSubTypes.Type(value = OvenMachine.class, name = "oven")
})
public abstract class Machine {
    /** Identificador único da máquina no sistema (ex: "MIX-01"). */
    private String machineId;

    /** Nome legível da máquina (ex: "Misturadora Massa Integral"). */
    private String machineName;

    /** Estado atual de operação da máquina (ex: RUNNING, STOPPED). */
    private MachineStatus status;

    /**
     * Carimbo de data/hora de quando a leitura dos sensores foi efetuada.
     * É inicializado automaticamente no momento da criação do objeto.
     */
    private LocalDateTime readAt;

    public Machine() {}

    /**
     * Construtor base para inicializar uma máquina.
     * O atributo {@code readAt} é definido automaticamente com {@code LocalDateTime.now()}.
     *
     * @param machineId   O ID único da máquina.
     * @param machineName O nome descritivo da máquina.
     * @param status      O estado operacional atual (Enum {@link MachineStatus}).
     */
    public Machine(String machineId, String machineName, MachineStatus status) {
        this.machineId = machineId;
        this.machineName = machineName;
        this.status = status;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Obtém o ID da máquina.
     * @return String contendo o ID.
     */
    public String getMachineId() { return machineId; }

    /**
     * Obtém o nome da máquina.
     * @return String contendo o nome.
     */
    public String getMachineName() { return machineName; }

    /**
     * Obtém o estado operacional da máquina.
     * @return O Enum {@link MachineStatus} correspondente.
     */
    public MachineStatus getStatus() { return status; }

    /**
     * Obtém a data e hora da leitura dos dados.
     * @return {@link LocalDateTime} do momento da instanciação.
     */
    public LocalDateTime getReadAt() { return readAt; }
}
