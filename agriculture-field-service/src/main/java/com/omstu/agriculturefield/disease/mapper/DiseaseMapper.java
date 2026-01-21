package com.omstu.agriculturefield.disease.mapper;

import com.omstu.agriculturefield.crop.model.CropType;
import com.omstu.agriculturefield.disease.dto.DiseaseRequest;
import com.omstu.agriculturefield.disease.dto.DiseaseResponse;
import com.omstu.agriculturefield.disease.model.Disease;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DiseaseMapper {

    @Mapping(target = "affectedCropIds", source = "affectedCrops", qualifiedByName = "cropsToIds")
    DiseaseResponse toResponse(Disease disease);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "affectedCrops", ignore = true)
    Disease toEntity(DiseaseRequest request);

    @Named("cropsToIds")
    default Set<Long> cropsToIds(Set<CropType> crops) {
        if (crops == null) {
            return Set.of();
        }
        return crops.stream()
                .map(CropType::getId)
                .collect(Collectors.toSet());
    }
}
