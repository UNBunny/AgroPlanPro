package com.omstu.agriculturefield.rotation.service.impl;

import com.omstu.agriculturefield.crop.model.CropType;
import com.omstu.agriculturefield.crop.repository.CropTypeRepository;
import com.omstu.agriculturefield.rotation.dto.CropRotationRuleRequest;
import com.omstu.agriculturefield.rotation.dto.CropRotationRuleResponse;
import com.omstu.agriculturefield.rotation.mapper.CropRotationRuleMapper;
import com.omstu.agriculturefield.rotation.model.CropRotationRule;
import com.omstu.agriculturefield.rotation.repository.CropRotationRuleRepository;
import com.omstu.agriculturefield.rotation.service.CropRotationRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CropRotationRuleServiceImpl implements CropRotationRuleService {

    private final CropRotationRuleRepository cropRotationRuleRepository;
    private final CropRotationRuleMapper cropRotationRuleMapper;
    private final CropTypeRepository cropTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CropRotationRuleResponse> getAll() {
        return cropRotationRuleRepository.findAll()
                .stream()
                .map(cropRotationRuleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CropRotationRuleResponse getById(Long id) {
        return cropRotationRuleRepository.findById(id)
                .map(cropRotationRuleMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Crop rotation rule not found with id: " + id));
    }

    @Override
    @Transactional
    public CropRotationRuleResponse create(CropRotationRuleRequest request) {
        CropType predecessor = cropTypeRepository.findById(request.predecessorCropId())
                .orElseThrow(() -> new RuntimeException("Crop type not found with id: " + request.predecessorCropId()));
        CropType successor = cropTypeRepository.findById(request.successorCropId())
                .orElseThrow(() -> new RuntimeException("Crop type not found with id: " + request.successorCropId()));

        CropRotationRule rule = cropRotationRuleMapper.toEntity(request);
        rule.setPredecessorCrop(predecessor);
        rule.setSuccessorCrop(successor);

        CropRotationRule saved = cropRotationRuleRepository.save(rule);
        log.info("Created crop rotation rule with id: {}", saved.getId());
        return cropRotationRuleMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CropRotationRuleResponse update(Long id, CropRotationRuleRequest request) {
        CropRotationRule existing = cropRotationRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop rotation rule not found with id: " + id));

        CropType predecessor = cropTypeRepository.findById(request.predecessorCropId())
                .orElseThrow(() -> new RuntimeException("Crop type not found with id: " + request.predecessorCropId()));
        CropType successor = cropTypeRepository.findById(request.successorCropId())
                .orElseThrow(() -> new RuntimeException("Crop type not found with id: " + request.successorCropId()));

        existing.setPredecessorCrop(predecessor);
        existing.setSuccessorCrop(successor);
        existing.setAllowed(request.allowed());
        existing.setMinGapYears(request.minGapYears());
        existing.setReason(request.reason());

        CropRotationRule updated = cropRotationRuleRepository.save(existing);
        log.info("Updated crop rotation rule with id: {}", updated.getId());
        return cropRotationRuleMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CropRotationRule rule = cropRotationRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop rotation rule not found with id: " + id));
        cropRotationRuleRepository.delete(rule);
        log.info("Deleted crop rotation rule with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropRotationRuleResponse> findByPredecessorCropId(Long predecessorCropId) {
        return cropRotationRuleRepository.findByPredecessorCropId(predecessorCropId)
                .stream()
                .map(cropRotationRuleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropRotationRuleResponse> findAllowedByPredecessorCropId(Long predecessorCropId) {
        return cropRotationRuleRepository.findAllowedByPredecessorCropId(predecessorCropId)
                .stream()
                .map(cropRotationRuleMapper::toResponse)
                .collect(Collectors.toList());
    }
}
