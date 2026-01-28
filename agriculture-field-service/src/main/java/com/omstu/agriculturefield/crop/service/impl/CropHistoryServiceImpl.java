package com.omstu.agriculturefield.crop.service.impl;

import com.omstu.agriculturefield.common.service.BaseService;
import com.omstu.agriculturefield.crop.dto.CropHistoryRequest;
import com.omstu.agriculturefield.crop.dto.CropHistoryResponse;
import com.omstu.agriculturefield.crop.mapper.CropHistoryMapper;
import com.omstu.agriculturefield.crop.model.CropHistory;
import com.omstu.agriculturefield.crop.model.CropType;
import com.omstu.agriculturefield.crop.model.CropVariety;
import com.omstu.agriculturefield.crop.repository.CropHistoryRepository;
import com.omstu.agriculturefield.crop.repository.CropTypeRepository;
import com.omstu.agriculturefield.crop.repository.CropVarietyRepository;
import com.omstu.agriculturefield.field.model.AgriculturalField;
import com.omstu.agriculturefield.field.repository.AgriculturalFieldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CropHistoryServiceImpl implements BaseService<CropHistoryRequest, CropHistoryResponse, Long> {
    private final CropHistoryRepository cropHistoryRepository;
    private final CropHistoryMapper cropHistoryMapper;
    private final AgriculturalFieldRepository agriculturalFieldRepository;
    private final CropTypeRepository cropTypeRepository;
    private final CropVarietyRepository cropVarietyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CropHistoryResponse> getAll() {
        return cropHistoryRepository.findAll()
                .stream()
                .map(cropHistoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CropHistoryResponse getById(Long id) {
        return cropHistoryRepository.findById(id)
                .map(cropHistoryMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Crop history not found with id: " + id));
    }

    @Override
    @Transactional
    public CropHistoryResponse create(CropHistoryRequest request) {
        AgriculturalField field = agriculturalFieldRepository.findById(request.fieldId())
                .orElseThrow(() -> new RuntimeException("Agricultural field not found with id: " + request.fieldId()));

        CropType cropType = cropTypeRepository.findById(request.cropTypeId())
                .orElseThrow(() -> new RuntimeException("Crop type not found with id: " + request.cropTypeId()));

        CropHistory cropHistory = cropHistoryMapper.toEntity(request);
        cropHistory.setField(field);
        cropHistory.setCropType(cropType);

        // CropVariety опционален
        if (request.cropVarietyId() != null) {
            CropVariety cropVariety = cropVarietyRepository.findById(request.cropVarietyId())
                    .orElseThrow(() -> new RuntimeException("Crop variety not found with id: " + request.cropVarietyId()));
            cropHistory.setCropVariety(cropVariety);
        }

        CropHistory saved = cropHistoryRepository.save(cropHistory);
        log.info("Created crop history with id: {}", saved.getId());
        return cropHistoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CropHistoryResponse update(Long id, CropHistoryRequest request) {
        CropHistory existing = cropHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop history not found with id: " + id));

        AgriculturalField field = agriculturalFieldRepository.findById(request.fieldId())
                .orElseThrow(() -> new RuntimeException("Agricultural field not found with id: " + request.fieldId()));

        CropType cropType = cropTypeRepository.findById(request.cropTypeId())
                .orElseThrow(() -> new RuntimeException("Crop type not found with id: " + request.cropTypeId()));

        existing.setField(field);
        existing.setCropType(cropType);
        existing.setPlantingDate(request.plantingDate());
        existing.setActualHarvestDate(request.actualHarvestDate());
        existing.setExpectedHarvestDate(request.expectedHarvestDate());
        existing.setSeedAmountKgPerHa(request.seedAmountKgPerHa());
        existing.setSeedDepthCm(request.seedDepthCm());
        existing.setPlantSpacingCm(request.plantSpacingCm());
        existing.setActualYieldKg(request.actualYieldKg());
        existing.setExpectedYieldKg(request.expectedYieldKg());
        existing.setPlantingStatus(request.plantingStatus());
        existing.setNotes(request.notes());
        existing.setWeatherConditions(request.weatherConditions());

        // CropVariety опционален
        if (request.cropVarietyId() != null) {
            CropVariety cropVariety = cropVarietyRepository.findById(request.cropVarietyId())
                    .orElseThrow(() -> new RuntimeException("Crop variety not found with id: " + request.cropVarietyId()));
            existing.setCropVariety(cropVariety);
        } else {
            existing.setCropVariety(null);
        }

        CropHistory updated = cropHistoryRepository.save(existing);
        log.info("Updated crop history with id: {}", updated.getId());
        return cropHistoryMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CropHistory cropHistory = cropHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop history not found with id: " + id));
        cropHistoryRepository.delete(cropHistory);
        log.info("Deleted crop history with id: {}", id);
    }
}
