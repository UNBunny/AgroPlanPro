package com.omstu.agriculturefield.crop.mapper;

import com.omstu.agriculturefield.crop.dto.CropVarietyRequest;
import com.omstu.agriculturefield.crop.dto.CropVarietyResponse;
import com.omstu.agriculturefield.crop.model.CropVariety;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CropVarietyMapper {
    CropVariety toEntity(CropVarietyRequest request);

    CropVarietyResponse toResponse(CropVariety cropVariety);

    CropVariety toEntityWithId(Long id, CropVarietyRequest request);
}
