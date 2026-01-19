package com.omstu.agriculturefield.crop.service;

import com.omstu.agriculturefield.crop.dto.CropVarietyRequest;
import com.omstu.agriculturefield.crop.dto.CropVarietyResponse;

import java.util.List;

public interface CropVarietyService {
    List<CropVarietyResponse> getAllCropVarieties();

    CropVarietyResponse getCropVarietyById(Long id);

    CropVarietyResponse createCropVariety(CropVarietyRequest cropVariety);

    CropVarietyResponse updateCropVariety(Long id, CropVarietyRequest cropVariety);

    void deleteCropVariety(Long id);
}
