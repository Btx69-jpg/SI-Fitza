package com.camunda.classes.RegistoLote;

import com.camunda.classes.RegistoLote.Enums.ProductionLine;
import com.camunda.classes.RegistoLote.Enums.TypeCleaning;

import javax.annotation.Nullable;

public class CleaningLine {
    private ProductionLine line;
    private TypeCleaning typeCleaning;
    private boolean isLineClear;
    private boolean isPackagingRemoved;
    private boolean isWasteEmptied;
    private boolean isConveyorSanitized;

    @Nullable
    private String observations;
    private boolean isCleaningApproved;

    public CleaningLine() {
    }

    public CleaningLine(ProductionLine line, TypeCleaning typeCleaning,
                        boolean isLineClear, boolean isPackagingRemoved,
                        boolean isWasteEmptied, boolean isConveyorSanitized,
                        boolean isCleaningApproved) {
        this(line, typeCleaning, isLineClear, isPackagingRemoved, isWasteEmptied, isConveyorSanitized, null, isCleaningApproved);
    }

    public CleaningLine(ProductionLine line, TypeCleaning typeCleaning,
                        boolean isLineClear, boolean isPackagingRemoved,
                        boolean isWasteEmptied, boolean isConveyorSanitized,
                        @Nullable String observations,
                        boolean isCleaningApproved) {
        this.line = line;
        this.typeCleaning = typeCleaning;
        this.isLineClear = isLineClear;
        this.isPackagingRemoved = isPackagingRemoved;
        this.isWasteEmptied = isWasteEmptied;
        this.isConveyorSanitized = isConveyorSanitized;
        this.observations = observations;
        this.isCleaningApproved = isCleaningApproved;
    }

    public TypeCleaning getTypeCleaning() {
        return typeCleaning;
    }

    public void setTypeCleaning(TypeCleaning typeCleaning) {
        this.typeCleaning = typeCleaning;
    }

    public ProductionLine getLine() {
        return line;
    }

    public void setLine(ProductionLine line) {
        this.line = line;
    }

    public boolean isLineClear() {
        return isLineClear;
    }

    public void setLineClear(boolean lineClear) {
        isLineClear = lineClear;
    }

    public boolean isWasteEmptied() {
        return isWasteEmptied;
    }

    public void setWasteEmptied(boolean wasteEmptied) {
        isWasteEmptied = wasteEmptied;
    }

    public boolean isPackagingRemoved() {
        return isPackagingRemoved;
    }

    public void setPackagingRemoved(boolean packagingRemoved) {
        isPackagingRemoved = packagingRemoved;
    }

    public boolean isConveyorSanitized() {
        return isConveyorSanitized;
    }

    public void setConveyorSanitized(boolean conveyorSanitized) {
        isConveyorSanitized = conveyorSanitized;
    }

    @Nullable
    public String getObservations() {
        return observations;
    }

    public void setObservations(@Nullable String observations) {
        this.observations = observations;
    }

    public boolean isCleaningApproved() {
        return isCleaningApproved;
    }

    public void setCleaningApproved(boolean cleaningApproved) {
        isCleaningApproved = cleaningApproved;
    }
}
