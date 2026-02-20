package com.omstu.agriculturefield.rotation.dto;

public record CropRotationRuleResponse(
        Long id,
        Long predecessorCropId,
        String predecessorCropName,
        Long successorCropId,
        String successorCropName,
        Boolean allowed,
        Integer minGapYears,
        String reason
) {}
