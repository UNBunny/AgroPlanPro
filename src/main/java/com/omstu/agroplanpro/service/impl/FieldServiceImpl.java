package com.omstu.agroplanpro.service.impl;

import com.omstu.agroplanpro.dto.AgriculturalFieldRequest;
import com.omstu.agroplanpro.dto.AgriculturalFieldResponse;
import com.omstu.agroplanpro.mapper.AgriculturalFieldMapper;
import com.omstu.agroplanpro.model.AgriculturalField;
import com.omstu.agroplanpro.repository.AgriculturalFieldRepository;
import com.omstu.agroplanpro.service.FieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {
    private final AgriculturalFieldRepository fieldRepository;
    private final AgriculturalFieldMapper fieldMapper;

    public AgriculturalFieldResponse createField(AgriculturalFieldRequest request) {
        AgriculturalField field = fieldMapper.toEntity(request);
        AgriculturalField savedField = fieldRepository.save(field);
        log.info("Agricultural field created with ID: {}", savedField.getId());
        return fieldMapper.toResponse(savedField);
    }

    public List<AgriculturalFieldResponse> getAllFields() {
        return fieldRepository.findAll().stream()
                .map(fieldMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AgriculturalFieldResponse getFieldById(Long id) {
        AgriculturalField field = fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found with id: " + id));
        return fieldMapper.toResponse(field);
    }
}