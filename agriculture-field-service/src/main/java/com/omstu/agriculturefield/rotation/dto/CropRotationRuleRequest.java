package com.omstu.agriculturefield.rotation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CropRotationRuleRequest(
        @NotNull Long predecessorCropId,
        @NotNull Long successorCropId,
        @NotNull Boolean allowed,
        @Min(0) Integer minGapYears,
        String reason
) {}
