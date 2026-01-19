package com.omstu.agriculturefield.mapper;

import com.omstu.agriculturefield.dto.crop.CropVarietyRequest;
import com.omstu.agriculturefield.dto.crop.CropVarietyResponse;
import com.omstu.agriculturefield.model.crop.CropVariety;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CropVarietyMapper {
    CropVariety toEntity(CropVarietyRequest request);

    CropVarietyResponse toResponse(CropVariety cropVariety);

    CropVariety toEntityWithId(Long id, CropVarietyRequest request);
}
