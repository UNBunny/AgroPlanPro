package com.omstu.agriculturefield.rotation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SeasonalWeatherDto(
        Integer year,
        Double precipOctMar,
        Double minTempWinter,
        Double precipAprMay,
        Double tempSumAprMay,
        Boolean frostRiskSpring,
        Double gtkAprMay,
        Double precipJunJul,
        Double tempSumJunJul,
        Integer heatStressJunJul,
        Integer extremeHeatJunJul,
        Double avgTempJunJul,
        Double gtkJunJul,
        Double precipAugSep,
        Double tempSumAugSep,
        Integer heatStressAugSep,
        Double gtkAugSep,
        Double gtkAprSep,
        Double tempSumAprSep,
        Integer totalHeatStressDays,
        Double minTempVegetation,
        Integer longestDryPeriod
) {}
