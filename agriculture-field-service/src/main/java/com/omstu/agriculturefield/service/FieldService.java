package com.omstu.agriculturefield.service;

import com.omstu.agriculturefield.dto.AgriculturalFieldRequest;
import com.omstu.agriculturefield.dto.AgriculturalFieldResponse;

import java.util.List;

public interface FieldService {
    AgriculturalFieldResponse createField(AgriculturalFieldRequest request);
    AgriculturalFieldResponse updateField(Long id, AgriculturalFieldRequest request);
    AgriculturalFieldResponse getFieldById(Long id);
    List<AgriculturalFieldResponse> getAllFields();
    void deleteField(Long id);
}
