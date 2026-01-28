package com.omstu.agriculturefield.crop.mapper;

import com.omstu.agriculturefield.crop.dto.CropTypeRequest;
import com.omstu.agriculturefield.crop.dto.CropTypeResponse;
import com.omstu.agriculturefield.crop.model.CropType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CropTypeMapper {

    CropType toEntity(CropTypeRequest cropType);

    CropTypeResponse toResponse(CropType cropType);

    CropType toEntityWithId(Long id, CropTypeRequest cropType);
}
