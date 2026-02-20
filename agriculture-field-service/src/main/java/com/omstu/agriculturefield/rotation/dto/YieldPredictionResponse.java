package com.omstu.agriculturefield.rotation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YieldPredictionResponse(
        @JsonProperty("predicted_yield") Double predictedYield
) {}
