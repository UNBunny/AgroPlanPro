package com.omstu.agriculturefield.field.service.impl;

import com.omstu.agriculturefield.field.dto.AgriculturalFieldRequest;
import com.omstu.agriculturefield.field.dto.AgriculturalFieldResponse;
import com.omstu.agriculturefield.field.mapper.AgriculturalFieldMapper;
import com.omstu.agriculturefield.field.model.AgriculturalField;
import com.omstu.agriculturefield.field.repository.AgriculturalFieldRepository;
import com.omstu.agriculturefield.field.service.FieldService;
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

    @Override
    public AgriculturalFieldResponse createField(AgriculturalFieldRequest request) {
        AgriculturalField field = fieldMapper.toEntity(request);
        AgriculturalField savedField = fieldRepository.save(field);
        log.info("Agricultural field created with ID: {}", savedField.getId());
        return fieldMapper.toResponse(savedField);
    }

    @Override
    public List<AgriculturalFieldResponse> getAllFields() {

        return fieldRepository.findAll().stream()
                .map(fieldMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AgriculturalFieldResponse getFieldById(Long id) {
        AgriculturalField field = fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found with id: " + id));
        return fieldMapper.toResponse(field);
    }

    @Override
    public AgriculturalFieldResponse updateField(Long id, AgriculturalFieldRequest request) {
        AgriculturalField field = fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found with id: " + id));
        AgriculturalField updatedField = fieldMapper.toEntityWithId(id, request);
        fieldRepository.save(updatedField);
        log.info("Agricultural field updated with ID: {}", updatedField.getId());
        return fieldMapper.toResponse(updatedField);
    }

    @Override
    public void deleteField(Long id) {
        fieldRepository.deleteById(id);
        log.info("Agricultural field deleted with ID: {}", id);
    }
}