package com.omstu.agriculturefield.controller;

import com.omstu.agriculturefield.dto.crop.CropTypeRequest;
import com.omstu.agriculturefield.dto.crop.CropTypeResponse;
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
@RequestMapping("/api/crop-type")
@RequiredArgsConstructor
public class CropTypeController {

    private final BaseService<CropTypeRequest, CropTypeResponse, Long> cropTypeService;

    @GetMapping
    public List<CropTypeResponse> getAllCropTypes() {
        return cropTypeService.getAll();
    }

    @GetMapping("/{id}")
    public CropTypeResponse getCropTypeById(@PathVariable Long id) {
        return cropTypeService.getById(id);
    }

    @PostMapping
    public CropTypeResponse createCropType(@RequestBody CropTypeRequest request) {
        return cropTypeService.create(request);
    }

    @PutMapping("/{id}")
    public CropTypeResponse updateCropType(@PathVariable Long id, @RequestBody CropTypeRequest request) {
        return cropTypeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCropType(@PathVariable Long id) {
        cropTypeService.delete(id);
    }
}
