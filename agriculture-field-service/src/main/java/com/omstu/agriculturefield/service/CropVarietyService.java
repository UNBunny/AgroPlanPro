package com.omstu.agriculturefield.service;

import com.omstu.agriculturefield.dto.crop.CropVarietyRequest;
import com.omstu.agriculturefield.dto.crop.CropVarietyResponse;

import java.util.List;

public interface CropVarietyService {
    List<CropVarietyResponse> getAllCropVarieties();

    CropVarietyResponse getCropVarietyById(Long id);

    CropVarietyResponse createCropVariety(CropVarietyRequest cropVariety);

    CropVarietyResponse updateCropVariety(Long id, CropVarietyRequest cropVariety);

    void deleteCropVariety(Long id);
}
