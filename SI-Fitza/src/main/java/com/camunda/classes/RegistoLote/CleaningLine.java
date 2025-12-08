package com.camunda.classes.RegistoLote;

import com.camunda.classes.RegistoLote.Enums.ProductionLine;
import com.camunda.classes.RegistoLote.Enums.TypeCleaning;

import javax.annotation.Nullable;

/**
 * Classe de domínio que representa o **Registo de Limpeza da Linha de Produção**
 * antes do início ou durante a produção de um novo Lote.
 * <p>
 * Esta classe é utilizada para documentar e garantir a conformidade (compliance)
 * com os padrões de higiene, sendo crucial para a segurança alimentar e rastreabilidade.
 * </p>
 *
 * <h3>Campos de Verificação (Checklist):</h3>
 * Os campos booleanos representam uma checklist de tarefas de limpeza que devem ser
 * verificadas e registadas.
 */
public class CleaningLine {
    /**
     * Linha de produção a que o registo de limpeza se refere, definida pelo Enum {@link ProductionLine}.
     */
    private ProductionLine line;

    /**
     * Tipo de limpeza realizada, definido pelo Enum {@link TypeCleaning} (ex: Limpeza Rápida, Limpeza Profunda).
     */
    private TypeCleaning typeCleaning;

    /**
     * Indica se a linha foi completamente limpa de resíduos do lote anterior.
     */
    private boolean isLineClear;

    /**
     * Indica se todos os materiais de embalagem foram removidos da área.
     */
    private boolean isPackagingRemoved;

    /**
     * Indica se os recipientes de resíduos e lixo foram esvaziados.
     */
    private boolean isWasteEmptied;

    /**
     * Indica se a correia transportadora (conveyor) foi higienizada/sanitizada.
     */
    private boolean isConveyorSanitized;

    /**
     * Observações adicionais sobre o procedimento de limpeza. Pode ser nulo ({@code @Nullable}).
     */
    @Nullable
    private String observations;

    /**
     * Indica o resultado final da inspeção: se a limpeza foi aprovada pelo supervisor.
     */
    private boolean isCleaningApproved;

    /**
     * Construtor padrão (default constructor) exigido pela biblioteca Jackson.
     */
    public CleaningLine() {
    }

    /**
     * Construtor auxiliar sem campo de observações (assume {@code observations = null}).
     *
     * @param line Linha de produção.
     * @param typeCleaning Tipo de limpeza realizada.
     * @param isLineClear Checklist: Linha limpa.
     * @param isPackagingRemoved Checklist: Embalagens removidas.
     * @param isWasteEmptied Checklist: Resíduos esvaziados.
     * @param isConveyorSanitized Checklist: Correia sanitizada.
     * @param isCleaningApproved Aprovação final da limpeza.
     */
    public CleaningLine(ProductionLine line, TypeCleaning typeCleaning,
                        boolean isLineClear, boolean isPackagingRemoved,
                        boolean isWasteEmptied, boolean isConveyorSanitized,
                        boolean isCleaningApproved) {
        this(line, typeCleaning, isLineClear, isPackagingRemoved, isWasteEmptied, isConveyorSanitized, null, isCleaningApproved);
    }

    /**
     * Construtor completo para inicializar o registo de limpeza.
     *
     * @param line Linha de produção.
     * @param typeCleaning Tipo de limpeza realizada.
     * @param isLineClear Checklist: Linha limpa.
     * @param isPackagingRemoved Checklist: Embalagens removidas.
     * @param isWasteEmptied Checklist: Resíduos esvaziados.
     * @param isConveyorSanitized Checklist: Correia sanitizada.
     * @param observations Observações adicionais (pode ser nulo).
     * @param isCleaningApproved Aprovação final da limpeza.
     */
    public CleaningLine(ProductionLine line, TypeCleaning typeCleaning,
                        boolean isLineClear, boolean isPackagingRemoved,
                        boolean isWasteEmptied, boolean isConveyorSanitized,
                        @Nullable String observations, boolean isCleaningApproved) {
        this.line = line;
        this.typeCleaning = typeCleaning;
        this.isLineClear = isLineClear;
        this.isPackagingRemoved = isPackagingRemoved;
        this.isWasteEmptied = isWasteEmptied;
        this.isConveyorSanitized = isConveyorSanitized;
        this.observations = observations;
        this.isCleaningApproved = isCleaningApproved;
    }

    /**
     * Obtém o tipo de limpeza realizada.
     * @return O {@link TypeCleaning} (Enum).
     */
    public TypeCleaning getTypeCleaning() {
        return typeCleaning;
    }

    /**
     * Define o tipo de limpeza.
     * @param typeCleaning O novo {@link TypeCleaning}.
     */
    public void setTypeCleaning(TypeCleaning typeCleaning) {
        this.typeCleaning = typeCleaning;
    }

    /**
     * Obtém a linha de produção associada.
     * @return A {@link ProductionLine} (Enum).
     */
    public ProductionLine getLine() {
        return line;
    }

    /**
     * Define a linha de produção.
     * @param line A nova {@link ProductionLine}.
     */
    public void setLine(ProductionLine line) {
        this.line = line;
    }

    /**
     * Verifica se a linha está limpa de resíduos.
     * @return {@code true} se sim.
     */
    public boolean isLineClear() {
        return isLineClear;
    }

    /**
     * Define o estado da limpeza da linha.
     * @param lineClear {@code true} se limpa.
     */
    public void setLineClear(boolean lineClear) {
        isLineClear = lineClear;
    }

    /**
     * Verifica se o desperdício foi esvaziado.
     * @return {@code true} se sim.
     */
    public boolean isWasteEmptied() {
        return isWasteEmptied;
    }

    /**
     * Define o estado do esvaziamento do desperdício.
     * @param wasteEmptied {@code true} se esvaziado.
     */
    public void setWasteEmptied(boolean wasteEmptied) {
        isWasteEmptied = wasteEmptied;
    }

    /**
     * Verifica se as embalagens foram removidas.
     * @return {@code true} se sim.
     */
    public boolean isPackagingRemoved() {
        return isPackagingRemoved;
    }

    /**
     * Define o estado da remoção de embalagens.
     * @param packagingRemoved {@code true} se removidas.
     */
    public void setPackagingRemoved(boolean packagingRemoved) {
        isPackagingRemoved = packagingRemoved;
    }

    /**
     * Verifica se a correia transportadora foi sanitizada.
     * @return {@code true} se sim.
     */
    public boolean isConveyorSanitized() {
        return isConveyorSanitized;
    }

    /**
     * Define o estado da sanitização da correia transportadora.
     * @param conveyorSanitized {@code true} se sanitizada.
     */
    public void setConveyorSanitized(boolean conveyorSanitized) {
        isConveyorSanitized = conveyorSanitized;
    }

    /**
     * Obtém as observações adicionais.
     * @return A string de observações, ou {@code null}.
     */
    @Nullable
    public String getObservations() {
        return observations;
    }

    /**
     * Define as observações adicionais.
     * @param observations A string de observações.
     */
    public void setObservations(@Nullable String observations) {
        this.observations = observations;
    }

    /**
     * Verifica o estado de aprovação da limpeza.
     * @return {@code true} se a limpeza foi aprovada pelo supervisor.
     */
    public boolean isCleaningApproved() {
        return isCleaningApproved;
    }

    /**
     * Define o estado de aprovação da limpeza.
     * @param cleaningApproved {@code true} se aprovada.
     */
    public void setCleaningApproved(boolean cleaningApproved) {
        isCleaningApproved = cleaningApproved;
    }
}
