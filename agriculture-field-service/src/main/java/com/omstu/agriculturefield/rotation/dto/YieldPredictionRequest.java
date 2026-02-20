package com.omstu.agriculturefield.rotation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record YieldPredictionRequest(
        String region,
        String crop,
        @JsonProperty("precip_oct_mar") Double precipOctMar,
        @JsonProperty("min_temp_winter") Double minTempWinter,
        @JsonProperty("precip_apr_may") Double precipAprMay,
        @JsonProperty("temp_sum_apr_may") Double tempSumAprMay,
        @JsonProperty("frost_risk_spring") Boolean frostRiskSpring,
        @JsonProperty("precip_jun_jul") Double precipJunJul,
        @JsonProperty("temp_sum_jun_jul") Double tempSumJunJul,
        @JsonProperty("heat_stress_jun_jul") Integer heatStressJunJul,
        @JsonProperty("gtk_jun_jul") Double gtkJunJul,
        @JsonProperty("precip_aug_sep") Double precipAugSep,
        @JsonProperty("temp_sum_aug_sep") Double tempSumAugSep,
        @JsonProperty("heat_stress_aug_sep") Integer heatStressAugSep,
        @JsonProperty("gtk_apr_sep") Double gtkAprSep,
        @JsonProperty("temp_sum_apr_sep") Double tempSumAprSep,
        @JsonProperty("total_heat_stress_days") Integer totalHeatStressDays
) {}
