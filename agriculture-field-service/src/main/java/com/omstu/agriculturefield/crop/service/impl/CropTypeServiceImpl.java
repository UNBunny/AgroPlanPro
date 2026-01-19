package com.omstu.agriculturefield.crop.service.impl;

import com.omstu.agriculturefield.crop.dto.CropTypeRequest;
import com.omstu.agriculturefield.crop.dto.CropTypeResponse;
import com.omstu.agriculturefield.crop.mapper.CropTypeMapper;
import com.omstu.agriculturefield.crop.model.CropType;
import com.omstu.agriculturefield.crop.repository.CropTypeRepository;
import com.omstu.agriculturefield.common.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CropTypeServiceImpl implements BaseService<CropTypeRequest, CropTypeResponse, Long> {
    private final CropTypeRepository cropTypeRepository;
    private final CropTypeMapper cropTypeMapper;

    @Override
    public List<CropTypeResponse> getAll() {
        return cropTypeRepository.findAll()
                .stream().map(cropTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CropTypeResponse getById(Long id) {
        return cropTypeRepository.findById(id)
                .map(cropTypeMapper::toResponse)
                .orElse(null);
    }

    @Override
    public CropTypeResponse create(CropTypeRequest cropTypeRequest) {
        CropType cropTypeEntity = cropTypeMapper.toEntity(cropTypeRequest);
        CropType savedCropType = cropTypeRepository.save(cropTypeEntity);
        return cropTypeMapper.toResponse(savedCropType);
    }

    @Override
    public CropTypeResponse update(Long id, CropTypeRequest cropTypeRequest) {
        CropType cropTypeEntity = cropTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop type not found with id: " + id));
        CropType updatedCropType = cropTypeMapper.toEntityWithId(id, cropTypeRequest);
        return cropTypeMapper.toResponse(updatedCropType);
    }

    @Override
    public void delete(Long id) {
        CropType cropType = cropTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop type not found with id: " + id));
        cropTypeRepository.delete(cropType);

    }
}
