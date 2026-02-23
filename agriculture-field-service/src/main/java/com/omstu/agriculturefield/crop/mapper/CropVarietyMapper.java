package com.omstu.agriculturefield.crop.mapper;

import com.omstu.agriculturefield.crop.dto.CropVarietyRequest;
import com.omstu.agriculturefield.crop.dto.CropVarietyResponse;
import com.omstu.agriculturefield.crop.model.CropVariety;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CropVarietyMapper {

    @Mapping(target = "diseaseResistance", ignore = true)
    @Mapping(target = "cropType", ignore = true)
    CropVariety toEntity(CropVarietyRequest request);

    @Mapping(target = "cropTypeId", source = "cropType.id")
    @Mapping(target = "cropTypeName", source = "cropType.name")
    CropVarietyResponse toResponse(CropVariety cropVariety);

    @Mapping(target = "diseaseResistance", ignore = true)
    @Mapping(target = "cropType", ignore = true)
    CropVariety toEntityWithId(Long id, CropVarietyRequest request);
}
