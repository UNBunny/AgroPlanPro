package com.omstu.agriculturefield.disease.dto;

import com.omstu.agriculturefield.disease.model.enums.RiskLevel;

import java.util.List;

public record DiseaseRiskItem(
        String diseaseName,
        String scientificName,
        RiskLevel riskLevel,
        Double probability,
        List<String> triggerFactors,
        String recommendation
) {}
