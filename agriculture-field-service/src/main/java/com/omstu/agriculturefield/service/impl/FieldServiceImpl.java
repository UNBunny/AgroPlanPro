package com.omstu.agriculturefield.service.impl;

import com.omstu.agriculturefield.dto.AgriculturalFieldRequest;
import com.omstu.agriculturefield.dto.AgriculturalFieldResponse;
import com.omstu.agriculturefield.mapper.AgriculturalFieldMapper;
import com.omstu.agriculturefield.model.AgriculturalField;
import com.omstu.agriculturefield.repository.AgriculturalFieldRepository;
import com.omstu.agriculturefield.service.FieldService;
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