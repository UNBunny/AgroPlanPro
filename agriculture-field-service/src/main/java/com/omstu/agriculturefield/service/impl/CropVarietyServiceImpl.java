package com.omstu.agriculturefield.service.impl;

import com.omstu.agriculturefield.dto.crop.CropVarietyRequest;
import com.omstu.agriculturefield.dto.crop.CropVarietyResponse;
import com.omstu.agriculturefield.mapper.CropVarietyMapper;
import com.omstu.agriculturefield.model.crop.CropVariety;
import com.omstu.agriculturefield.repository.CropVarietyRepository;
import com.omstu.agriculturefield.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CropVarietyServiceImpl implements BaseService<CropVarietyRequest, CropVarietyResponse, Long> {

    private final CropVarietyRepository cropVarietyRepository;
    private final CropVarietyMapper cropVarietyMapper;

    @Override
    public List<CropVarietyResponse> getAll() {
        return cropVarietyRepository.findAll().stream()
                .map(cropVarietyMapper::toResponse)
                .toList();
    }

    @Override
    public CropVarietyResponse getById(Long aLong) {
        return cropVarietyRepository.findById(aLong)
                .map(cropVarietyMapper::toResponse)
                .orElse(null);
    }

    @Override
    public CropVarietyResponse create(CropVarietyRequest request) {
        CropVariety cropVariety = cropVarietyMapper.toEntity(request);
        CropVariety savedCropVariety = cropVarietyRepository.save(cropVariety);
        return cropVarietyMapper.toResponse(savedCropVariety);
    }

    @Override
    public CropVarietyResponse update(Long aLong, CropVarietyRequest request) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }
}
