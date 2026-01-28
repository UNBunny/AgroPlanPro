package com.omstu.agriculturefield.crop.dto;

import com.omstu.agriculturefield.crop.model.enums.PlantingStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Date;

public record CropHistoryRequest(
        @NotNull(message = "ID поля обязателен")
        Long fieldId,

        @NotNull(message = "ID типа культуры обязателен")
        Long cropTypeId,

        Long cropVarietyId, // Может быть null

        @NotNull(message = "Дата посадки обязательна")
        Date plantingDate,

        Date actualHarvestDate,

        Date expectedHarvestDate,

        @NotNull(message = "Норма высева обязательна")
        @Positive(message = "Норма высева должна быть положительной")
        BigDecimal seedAmountKgPerHa,

        BigDecimal seedDepthCm,

        BigDecimal plantSpacingCm,

        BigDecimal actualYieldKg,

        BigDecimal expectedYieldKg,

        @NotNull(message = "Статус посадки обязателен")
        PlantingStatus plantingStatus,

        String notes,

        String weatherConditions
) {
}
