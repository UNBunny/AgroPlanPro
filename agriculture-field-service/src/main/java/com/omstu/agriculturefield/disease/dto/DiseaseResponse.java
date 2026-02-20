package com.omstu.agriculturefield.disease.dto;

import com.omstu.agriculturefield.disease.model.enums.DiseaseType;
import com.omstu.agriculturefield.disease.model.enums.RiskLevel;

import java.util.Set;

public record DiseaseResponse(
        Long id,
        String scientificName,
        String commonName,
        DiseaseType diseaseType,
        Set<Long> affectedCropIds,
        String symptoms,
        String preventionMeasures,
        String treatmentMethods,
        RiskLevel riskLevel,
        String activeSeason,
        String favorableConditions,
        String imageUrl,
        Boolean isActive
) {}
