package com.omstu.agriculturefield.disease.dto;

import com.omstu.agriculturefield.disease.model.enums.ResistanceLevel;

public record DiseaseResistanceResponse(
        Long id,
        Long diseaseId,
        String diseaseName,
        Long cropVarietyId,
        String cropVarietyName,
        ResistanceLevel resistanceLevel
) {}
