package com.omstu.agriculturefield.crop.dto;

import com.omstu.agriculturefield.crop.model.enums.PlantingStatus;

import java.math.BigDecimal;
import java.util.Date;

public record CropHistoryRequest(
        Long fieldId,
        Long cropTypeId,
        Long cropVarietyId,
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
) {}
