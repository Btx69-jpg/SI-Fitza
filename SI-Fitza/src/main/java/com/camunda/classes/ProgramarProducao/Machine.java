package com.camunda.classes.ProgramarProducao;

/**
 * Representa uma **Máquina** (Equipamento) de produção, utilizada no processo
 * de fabrico.
 *
 * <p>Esta classe modela o estado operacional de um equipamento, indicando se está
 * a funcionar corretamente ou se está avariado (necessita de manutenção).
 */
public class Machine {

    /**
     * O identificador único da máquina (ex: "MAQ-001").
     */
    private String id;
    /**
     * O nome descritivo da máquina (ex: "Forno Industrial").
     */
    private String name;
    /**
     * Estado operacional da máquina. {@code true} se estiver a funcionar
     * corretamente (OK), {@code false} se estiver avariada.
     */
    private boolean isWorking; // true = OK, false = Avariada
    /**
     * Uma descrição detalhada do estado atual da máquina (ex: "OPERACIONAL" ou "Falha no motor").
     */
    private String statusDescription;

    /**
     * Construtor padrão (necessário para serialização/desserialização em alguns frameworks).
     */
    public Machine() {}

    /**
     * Construtor para criar uma nova instância de Máquina.
     *
     * <p>Por defeito, a máquina é criada com o estado **operacional** ({@code isWorking = true})
     * e a descrição de estado como "OPERACIONAL".
     *
     * @param id O identificador único da máquina (ex: "MAQ-001").
     * @param name O nome descritivo da máquina (ex: "Forno Industrial").
     */
    public Machine(String id, String name) {
        this.id = id;
        this.name = name;
        this.isWorking = true; // Por defeito, começa a funcionar
        this.statusDescription = "OPERACIONAL";
    }

    /**
     * Define o estado da máquina como **avariada** (não operacional).
     *
     * <p>Muda o valor de {@code isWorking} para {@code false} e atualiza
     * a {@code statusDescription} com a razão da avaria.
     *
     * @param reason A razão ou descrição da avaria.
     */
    public void setBroken(String reason) {
        this.isWorking = false;
        this.statusDescription = reason;
    }

    /**
     * Verifica o estado operacional da máquina.
     *
     * @return {@code true} se a máquina estiver a funcionar (OK), {@code false} caso contrário.
     */
    public boolean isWorking() { return isWorking; }

    /**
     * Obtém o nome descritivo da máquina.
     *
     * @return O nome da máquina.
     */
    public String getName() { return name; }

    /**
     * Obtém a descrição do estado atual da máquina (ex: "OPERACIONAL" ou "Falha no motor").
     *
     * @return A descrição detalhada do estado.
     */
    public String getStatusDescription() { return statusDescription; }

    /**
     * Gera uma representação em String da máquina e do seu estado.
     *
     * @return Uma string formatada no padrão "Nome (ID): StatusDescription".
     */
    @Override
    public String toString() {
        return name + " (" + id + "): " + statusDescription;
    }
}
