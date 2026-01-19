package com.omstu.agriculturefield.controller;

import com.omstu.agriculturefield.dto.crop.CropVarietyRequest;
import com.omstu.agriculturefield.dto.crop.CropVarietyResponse;
import com.omstu.agriculturefield.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/crop-variety")
@RequiredArgsConstructor
public class CropVarietyController {
    private final BaseService<CropVarietyRequest, CropVarietyResponse, Long> cropVarietyService;

    @GetMapping
    public List<CropVarietyResponse> getAllCropVarieties() {
        return cropVarietyService.getAll();
    }

    @GetMapping("/{id}")
    public CropVarietyResponse getCropVarietyById(@PathVariable Long id) {
        return cropVarietyService.getById(id);
    }

    @PostMapping
    public CropVarietyResponse createCropVariety(@RequestBody CropVarietyRequest cropVarietyRequest) {
        return cropVarietyService.create(cropVarietyRequest);
    }

    @PutMapping("/{id}")
    public CropVarietyResponse updateCropVariety(@PathVariable Long id, @RequestBody CropVarietyRequest cropVarietyRequest) {
        return cropVarietyService.update(id, cropVarietyRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteCropVariety(@PathVariable Long id) {
        cropVarietyService.delete(id);
    }
}
