package com.omstu.agriculturefield.disease.dto;

import java.time.LocalDate;
import java.util.List;

public record DiseaseRiskResponse(
        Long fieldId,
        String fieldName,
        String cropTypeName,
        LocalDate forecastDate,
        List<DiseaseRiskItem> risks
) {}
