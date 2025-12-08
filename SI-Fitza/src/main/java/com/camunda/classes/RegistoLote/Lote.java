package com.camunda.classes.RegistoLote;

import com.camunda.classes.Cliente;
import com.camunda.classes.RawMaterialUsed;
import com.camunda.classes.RegistoLote.Enums.LoteState;
import com.camunda.classes.RegistoLote.Enums.TypePizza;
import com.camunda.classes.RegistoLote.machines.Machine;
import com.camunda.classes.RegistoLote.sensores.RoomSensor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um **Lote de Produção (Production Batch)** na fábrica de pizzas.
 * <p>
 * Esta é a classe central do sistema de **rastreabilidade (traceability)**. Agrega toda a informação
 * desde a matéria-prima utilizada, os registos de limpeza da linha, até à telemetria
 * das máquinas (MES) e os dados do cliente final. O seu estado é gerido ao longo
 * do processo automatizado no Camunda.
 * </p>
 * <h3>Ciclo de Vida:</h3>
 * <ul>
 * <li>O lote é criado com estado {@link LoteState#BLOCKED}.</li>
 * <li>Recebe matérias-primas (via {@link #addMaterialUsed(RawMaterialUsed)}) e validação de limpeza.</li>
 * <li>Processa dados das máquinas via {@link #addMachineReading(Machine)} e sensores via {@link #addSensorRoomReading(RoomSensor)}.</li>
 * <li>É desbloqueado para expedição ({@link LoteState#APROVED}) se passar no controlo de qualidade.</li>
 * </ul>
 */
public class Lote {
    /** Identificador único do lote (ex: "LOT-2025-001"). */
    private String loteId;

    /** Estado atual do lote (Bloqueado, Em Produção, Libertado, etc.), encapsulado em {@link StateLote}. */
    private StateLote loteState;

    /** Tipo de pizza produzida neste lote (ex: MARGHERITA, PEPPERONI). */
    private TypePizza typePizza;

    /** Quantidade total produzida neste lote (em unidades ou Kg). */
    private float producedQuantity;

    /**
     * Indica se este lote foi produzido para uma encomenda específica (true) ou para stock (false).
     * Anotado para garantir que o nome da propriedade JSON é "order".
     */
    @JsonProperty("order")
    private boolean isOrder;

    /** O cliente associado (apenas relevante se {@code isOrder} for true). */
    private Cliente cliente;

    /** Registo histórico da telemetria das máquinas durante a produção deste lote. */
    private List<Machine> machineReadings;

    /** Registo histórico das leituras dos sensores de ambiente durante a produção. */
    private List<RoomSensor> roomSensors;

    /** Lista de matérias-primas consumidas na produção deste lote. */
    private List<RawMaterialUsed> rawMaterialUsed;

    /**
     * Construtor vazio necessário para serialização/deserialização (Jackson/JSON).
     * <p>
     * Garante que todas as listas internas são inicializadas como {@link ArrayList}
     * para evitar {@code NullPointerExceptions} se a deserialização for parcial.
     * </p>
     */
    public Lote() {
        this.machineReadings = new ArrayList<>();
        this.roomSensors = new ArrayList<>();
        this.rawMaterialUsed = new ArrayList<>();
    }

    /**
     * Construtor completo para iniciar um novo Lote de Produção.
     * <p>
     * O estado inicial é definido automaticamente como {@link LoteState#BLOCKED} por segurança,
     * através da criação de um novo {@link StateLote}.
     * </p>
     *
     * @param loteId Identificador único do lote.
     * @param typePizza Tipo de produto a fabricar.
     * @param isOrder Se é uma encomenda personalizada.
     * @param producedQuantity Quantidade planeada/produzida.
     * @param cliente Cliente associado (pode ser null se for stock).
     */
    public Lote(String loteId, TypePizza typePizza, boolean isOrder, float producedQuantity, Cliente cliente) {
        this.loteId = loteId;
        this.loteState = new StateLote(null, LoteState.BLOCKED);
        this.typePizza = typePizza;
        this.isOrder = isOrder;
        this.producedQuantity = producedQuantity;
        this.cliente = cliente;
        this.rawMaterialUsed = new ArrayList<>();
        this.machineReadings = new ArrayList<>();
        this.roomSensors = new ArrayList<>();
    }

    /**
     * Adiciona um registo de telemetria de uma máquina ao histórico do lote.
     * Usado pela integração com o sistema MES, respeitando o encapsulamento.
     *
     * @param machine Objeto da máquina (Mixer, Oven, etc.) com os dados lidos.
     */
    public void addMachineReading(Machine machine) {
        if (this.machineReadings == null) {
            this.machineReadings = new ArrayList<>();
        }
        this.machineReadings.add(machine);
    }

    /**
     * Adiciona um registo de leitura de um sensor de ambiente ao histórico do lote.
     * Usado pela integração com sistemas IoT, respeitando o encapsulamento.
     *
     * @param roomSensor Objeto do sensor (Temperatura, Humidade, etc.) com os dados lidos.
     */
    public void addSensorRoomReading(RoomSensor roomSensor) {
        if (this.roomSensors == null) {
            this.roomSensors = new ArrayList<>();
        }
        this.roomSensors.add(roomSensor);
    }

    /**
     * Adiciona um registo de matéria-prima consumida ao histórico do lote.
     * Usado para rastreabilidade dos ingredientes.
     *
     * @param rawMaterialUsed O objeto {@link RawMaterialUsed} contendo o material, validade e quantidade.
     */
    public void addMaterialUsed(RawMaterialUsed rawMaterialUsed) {
        if (this.rawMaterialUsed == null) {
            this.rawMaterialUsed = new ArrayList<>();
        }

        this.rawMaterialUsed.add(rawMaterialUsed);
    }

    /**
     * Obtém o identificador único do lote.
     * @return O ID do lote.
     */
    public String getLoteId() { return loteId; }

    /**
     * Obtém o estado atual do lote.
     * @return O objeto {@link StateLote} contendo o estado principal e a razão de descarte (se houver).
     */
    public StateLote getLoteState() { return loteState; }

    /**
     * Define o novo estado do lote.
     * @param loteState O novo objeto {@link StateLote}.
     */
    public void setLoteState(StateLote loteState) { this.loteState = loteState; }

    /**
     * Obtém o tipo de pizza produzida.
     * @return O {@link TypePizza} (Enum).
     */
    public TypePizza getTypePizza() { return typePizza; }

    /**
     * Obtém a quantidade total produzida.
     * @return A quantidade produzida (float).
     */
    public float getProducedQuantity() { return producedQuantity; }

    /**
     * Verifica se o lote é uma encomenda.
     * @return {@code true} se for uma encomenda, {@code false} se for para stock.
     */
    @JsonProperty("order")
    public boolean isOrder() { return isOrder; }

    /**
     * Obtém o cliente associado ao lote (se for encomenda).
     * @return O objeto {@link Cliente} ou {@code null}.
     */
    public Cliente getCliente() { return cliente; }

    /**
     * Obtém a lista imutável das leituras de máquinas.
     * @return A lista de {@link Machine} readings.
     */
    public List<Machine> getMachineReadings() { return machineReadings; }

    /**
     * Obtém a lista imutável das leituras de sensores de ambiente.
     * @return A lista de {@link RoomSensor} readings.
     */
    public List<RoomSensor> getRoomSensors() { return roomSensors; }

    /**
     * Obtém a lista imutável das matérias-primas utilizadas.
     * @return A lista de {@link RawMaterialUsed}.
     */
    public List<RawMaterialUsed> getRawMaterialUsed() { return rawMaterialUsed; }
}