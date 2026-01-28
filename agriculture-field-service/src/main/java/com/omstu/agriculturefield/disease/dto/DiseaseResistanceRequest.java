package com.omstu.agriculturefield.disease.dto;

import com.omstu.agriculturefield.disease.model.enums.ResistanceLevel;
import jakarta.validation.constraints.NotNull;

public record DiseaseResistanceRequest(
        @NotNull(message = "ID болезни обязателен")
        Long diseaseId,

        @NotNull(message = "ID сорта культуры обязателен")
        Long cropVarietyId,

        @NotNull(message = "Уровень устойчивости обязателен")
        ResistanceLevel resistanceLevel
) {
}
