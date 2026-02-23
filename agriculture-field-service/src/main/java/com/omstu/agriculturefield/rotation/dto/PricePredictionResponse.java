package com.omstu.agriculturefield.rotation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PricePredictionResponse(
        @JsonProperty("predicted_price") Double predictedPrice
) {}
