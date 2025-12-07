package com.omstu.agriculturefield.service;

import com.omstu.agriculturefield.dto.crop.CropTypeRequest;
import com.omstu.agriculturefield.dto.crop.CropTypeResponse;
import com.omstu.agriculturefield.model.crop.CropType;

import java.util.List;

public interface CropTypeService {
    List<CropTypeResponse> getAllCropTypes();
    CropTypeResponse getCropTypeById(Long id);
    CropTypeResponse createCropType(CropTypeRequest cropType);
    CropTypeResponse updateCropType(Long id, CropTypeRequest cropType);
    void deleteCropType(Long id);
}
