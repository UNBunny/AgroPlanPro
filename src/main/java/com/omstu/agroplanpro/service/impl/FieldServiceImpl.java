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
}
