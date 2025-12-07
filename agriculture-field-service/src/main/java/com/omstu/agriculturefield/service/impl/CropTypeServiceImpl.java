package com.omstu.agriculturefield.service.impl;

import com.omstu.agriculturefield.dto.crop.CropTypeRequest;
import com.omstu.agriculturefield.dto.crop.CropTypeResponse;
import com.omstu.agriculturefield.mapper.CropTypeMapper;
import com.omstu.agriculturefield.model.crop.CropType;
import com.omstu.agriculturefield.repository.CropTypeRepository;
import com.omstu.agriculturefield.service.CropTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CropTypeServiceImpl implements CropTypeService {
    private final CropTypeRepository cropTypeRepository;
    private final CropTypeMapper cropTypeMapper;

    @Override
    public List<CropTypeResponse> getAllCropTypes() {
        return cropTypeRepository.findAll()
                .stream().map(cropTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CropTypeResponse getCropTypeById(Long id) {
        return cropTypeRepository.findById(id)
                .map(cropTypeMapper::toResponse)
                .orElse(null);
    }

    @Override
    public CropTypeResponse createCropType(CropTypeRequest cropType) {
        CropType cropTypeEntity = cropTypeMapper.toEntity(cropType);
        CropType savedCropType = cropTypeRepository.save(cropTypeEntity);
        return cropTypeMapper.toResponse(savedCropType);
    }

    @Override
    public CropTypeResponse updateCropType(Long id, CropTypeRequest cropType) {
        CropType cropTypeEntity = cropTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop type not found with id: " + id));
        CropType updatedCropType = cropTypeMapper.toEntityWithId(id, cropType);
        return cropTypeMapper.toResponse(updatedCropType);
    }


    @Override
    public void deleteCropType(Long id) {
        CropType cropType = cropTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop type not found with id: " + id));
        cropTypeRepository.delete(cropType);
    }
}
