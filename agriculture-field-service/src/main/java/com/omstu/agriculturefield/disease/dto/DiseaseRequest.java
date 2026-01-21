package com.omstu.agriculturefield.disease.dto;

import com.omstu.agriculturefield.disease.model.enums.DiseaseType;
import com.omstu.agriculturefield.disease.model.enums.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record DiseaseRequest(
        @NotBlank(message = "Научное название обязательно")
        @Size(max = 200, message = "Научное название не должно превышать 200 символов")
        String scientificName,

        @NotBlank(message = "Общепринятое название обязательно")
        @Size(max = 200, message = "Общепринятое название не должно превышать 200 символов")
        String commonName,

        @NotNull(message = "Тип болезни обязателен")
        DiseaseType diseaseType,

        Set<Long> affectedCropIds, // ID культур, которые поражает болезнь

        @Size(max = 1000, message = "Симптомы не должны превышать 1000 символов")
        String symptoms,

        @Size(max = 1000, message = "Меры профилактики не должны превышать 1000 символов")
        String preventionMeasures,

        @Size(max = 1000, message = "Методы лечения не должны превышать 1000 символов")
        String treatmentMethods,

        @NotNull(message = "Уровень риска обязателен")
        RiskLevel riskLevel,

        @Size(max = 100, message = "Активный сезон не должен превышать 100 символов")
        String activeSeason,

        @Size(max = 500, message = "Благоприятные условия не должны превышать 500 символов")
        String favorableConditions,

        @Size(max = 500, message = "URL изображения не должен превышать 500 символов")
        String imageUrl,

        Boolean isActive
) {
}
