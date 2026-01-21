package com.omstu.agriculturefield.crop.dto;

import com.omstu.agriculturefield.crop.model.enums.PlantingStatus;

import java.math.BigDecimal;
import java.util.Date;

public record CropHistoryResponse(
        Long id,

        Long fieldId,

        String fieldName,

        Long cropTypeId,

        String cropTypeName,

        Long cropVarietyId,

        String cropVarietyName,

        Date plantingDate,

        Date actualHarvestDate,

        Date expectedHarvestDate,

        BigDecimal seedAmountKgPerHa,

        BigDecimal seedDepthCm,

        BigDecimal plantSpacingCm,

        BigDecimal actualYieldKg,

        BigDecimal expectedYieldKg,

        PlantingStatus plantingStatus,

        String notes,

        String weatherConditions
) {
}
