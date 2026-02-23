package com.omstu.agriculturefield.rotation.dto;

public record CropRecommendationItem(
        Long cropTypeId,
        String cropTypeName,
        Boolean rotationCompliant,
        String rotationViolationReason,
        Double predictedYieldCentnersPerHa,
        Double predictedPriceRubPerTon,
        Double estimatedProfitRubPerHa,
        Integer rank
) {}
