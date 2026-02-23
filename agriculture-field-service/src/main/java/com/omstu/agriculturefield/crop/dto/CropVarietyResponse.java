package com.omstu.agriculturefield.crop.dto;

import com.omstu.agriculturefield.crop.model.enums.ToleranceLevel;

import java.math.BigDecimal;

public record CropVarietyResponse(
        Long id,
        String name,
        Long cropTypeId,
        String cropTypeName,
        String seedProducer,
        Integer maturationDays,
        ToleranceLevel droughtTolerance,
        ToleranceLevel frostTolerance,
        BigDecimal recommendedSeedingRateKgPerHa,
        BigDecimal seedCostPerKg,
        Boolean isHybrid,
        String notes
) {}
