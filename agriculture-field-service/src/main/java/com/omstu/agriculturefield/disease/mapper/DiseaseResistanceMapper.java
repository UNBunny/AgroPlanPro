package com.omstu.agriculturefield.disease.mapper;

import com.omstu.agriculturefield.disease.dto.DiseaseResistanceRequest;
import com.omstu.agriculturefield.disease.dto.DiseaseResistanceResponse;
import com.omstu.agriculturefield.disease.model.DiseaseResistance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiseaseResistanceMapper {

    @Mapping(target = "diseaseId", source = "disease.id")
    @Mapping(target = "diseaseName", source = "disease.commonName")
    @Mapping(target = "cropVarietyId", source = "cropVariety.id")
    @Mapping(target = "cropVarietyName", source = "cropVariety.name")
    DiseaseResistanceResponse toResponse(DiseaseResistance diseaseResistance);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "disease", ignore = true)
    @Mapping(target = "cropVariety", ignore = true)
    DiseaseResistance toEntity(DiseaseResistanceRequest request);
}
