package com.omstu.agriculturefield.disease.dto;

import com.omstu.agriculturefield.disease.model.enums.ResistanceLevel;

public record DiseaseResistanceRequest(
        Long diseaseId,
        Long cropVarietyId,
        ResistanceLevel resistanceLevel
) {}
