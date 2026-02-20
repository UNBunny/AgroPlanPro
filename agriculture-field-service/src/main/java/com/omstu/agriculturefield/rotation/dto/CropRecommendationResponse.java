package com.omstu.agriculturefield.rotation.dto;

import java.util.List;

public record CropRecommendationResponse(
        Long fieldId,
        String fieldName,
        Integer targetYear,
        List<CropRecommendationItem> recommendations
) {}
