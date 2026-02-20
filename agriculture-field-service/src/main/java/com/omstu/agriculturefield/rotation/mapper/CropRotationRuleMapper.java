package com.omstu.agriculturefield.rotation.mapper;

import com.omstu.agriculturefield.rotation.dto.CropRotationRuleRequest;
import com.omstu.agriculturefield.rotation.dto.CropRotationRuleResponse;
import com.omstu.agriculturefield.rotation.model.CropRotationRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CropRotationRuleMapper {

    @Mapping(target = "predecessorCropId", source = "predecessorCrop.id")
    @Mapping(target = "predecessorCropName", source = "predecessorCrop.name")
    @Mapping(target = "successorCropId", source = "successorCrop.id")
    @Mapping(target = "successorCropName", source = "successorCrop.name")
    CropRotationRuleResponse toResponse(CropRotationRule rule);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "predecessorCrop", ignore = true)
    @Mapping(target = "successorCrop", ignore = true)
    CropRotationRule toEntity(CropRotationRuleRequest request);
}
