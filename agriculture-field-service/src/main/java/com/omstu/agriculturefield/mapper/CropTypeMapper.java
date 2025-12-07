package com.omstu.agriculturefield.mapper;

import com.omstu.agriculturefield.dto.crop.CropTypeRequest;
import com.omstu.agriculturefield.dto.crop.CropTypeResponse;
import com.omstu.agriculturefield.model.crop.CropType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CropTypeMapper {

    CropType toEntity(CropTypeRequest cropType);

    CropTypeResponse toResponse(CropType cropType);

    CropType toEntityWithId(Long id, CropTypeRequest cropType);
}
