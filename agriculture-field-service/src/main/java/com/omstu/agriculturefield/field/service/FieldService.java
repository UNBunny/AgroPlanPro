package com.omstu.agriculturefield.field.service;

import com.omstu.agriculturefield.field.dto.AgriculturalFieldRequest;
import com.omstu.agriculturefield.field.dto.AgriculturalFieldResponse;

import java.util.List;

public interface FieldService {
    AgriculturalFieldResponse createField(AgriculturalFieldRequest request);
    AgriculturalFieldResponse updateField(Long id, AgriculturalFieldRequest request);
    AgriculturalFieldResponse getFieldById(Long id);
    List<AgriculturalFieldResponse> getAllFields();
    void deleteField(Long id);
}
