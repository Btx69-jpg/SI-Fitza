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
 * Representa um Lote de Produção (Production Batch) na fábrica de pizzas.
 * <p>
 * Esta é a classe central do sistema de rastreabilidade. Agrega toda a informação
 * desde a matéria-prima utilizada, os registos de limpeza da linha, até à telemetria
 * das máquinas (MES) e o cliente final.
 * </p>
 * <h3>Ciclo de Vida:</h3>
 * <ul>
 * <li>O lote é criado com estado {@link LoteState#BLOCKED}.</li>
 * <li>Recebe matérias-primas e validação de limpeza.</li>
 * <li>Processa dados das máquinas via {@link #addMachineReading(Machine)}.</li>
 * <li>É desbloqueado para expedição se passar no controlo de qualidade.</li>
 * </ul>
 *
 */
public class Lote {
    /** Identificador único do lote (ex: "LOT-2025-001"). */
    private String loteId;

    /** Estado atual do lote (Bloqueado, Em Produção, Libertado, etc.). */
    private LoteState loteState;

    /** Tipo de pizza produzida neste lote (ex: MARGHERITA, PEPPERONI). */
    private TypePizza typePizza;

    /** Quantidade total produzida neste lote (em unidades ou Kg). */
    private float producedQuantity;

    /** Indica se este lote foi produzido para uma encomenda específica (true) ou para stock (false). */
    @JsonProperty("order")
    private boolean isOrder;

    /** O cliente associado (apenas relevante se {@code isOrder} for true). */
    private Cliente cliente;

    /** Registo histórico da telemetria das máquinas durante a produção deste lote. */
    private List<Machine> machineReadings;

    private List<RoomSensor> roomSensors;

    /** Lista de matérias-primas consumidas na produção deste lote. */
    private List<RawMaterialUsed> rawMaterialUsed;

    /** Registos das operações de limpeza efetuadas na linha antes/durante o lote. */
    private List<CleaningLine> cleaningLine;

    /**
     * Construtor vazio necessário para serialização/deserialização (Jackson/JSON).
     */
    public Lote() {
        this.machineReadings = new ArrayList<>();
        this.roomSensors = new ArrayList<>();
        this.rawMaterialUsed = new ArrayList<>();
        this.cleaningLine = new ArrayList<>();
    }

    /**
     * Construtor completo para iniciar um novo Lote de Produção.
     * <p>
     * O estado inicial é definido automaticamente como {@link LoteState#BLOCKED} por segurança.
     * As listas são inicializadas mesmo que os parâmetros sejam nulos, para evitar NullPointerExceptions.
     * </p>
     *
     * @param loteId           Identificador único do lote.
     * @param typePizza        Tipo de produto a fabricar.
     * @param isOrder          Se é uma encomenda personalizada.
     * @param producedQuantity Quantidade planeada/produzida.
     * @param cliente          Cliente associado (pode ser null se for stock).
     * @param rawMaterialUsed  Lista inicial de materiais usados.
     * @param cleaningLine     Lista inicial de registos de limpeza.
     */
    public Lote(String loteId, TypePizza typePizza, boolean isOrder,
                float producedQuantity, Cliente cliente,
                List<RawMaterialUsed> rawMaterialUsed, List<CleaningLine> cleaningLine) {

        this.loteId = loteId;
        this.loteState = LoteState.BLOCKED;
        this.typePizza = typePizza;
        this.isOrder = isOrder;
        this.producedQuantity = producedQuantity;
        this.cliente = cliente;
        this.rawMaterialUsed = (rawMaterialUsed != null) ? rawMaterialUsed : new ArrayList<>();
        this.cleaningLine = (cleaningLine != null) ? cleaningLine : new ArrayList<>();
        this.machineReadings = new ArrayList<>();
        this.roomSensors = new ArrayList<>();
    }

    /**
     * Adiciona um registo de telemetria de uma máquina ao histórico do lote.
     * Usado pela integração com o sistema MES.
     *
     * @param machine Objeto da máquina (Mixer, Oven, etc.) com os dados lidos.
     */
    public void addMachineReading(Machine machine) {
        if (this.machineReadings == null) {
            this.machineReadings = new ArrayList<>();
        }
        this.machineReadings.add(machine);
    }

    public void addSensorRoomReading(RoomSensor roomSensor) {
        if (this.roomSensors == null) {
            this.roomSensors = new ArrayList<>();
        }
        this.roomSensors.add(roomSensor);
    }
}
