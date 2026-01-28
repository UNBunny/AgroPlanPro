package com.omstu.agriculturefield.crop.controller;

import com.omstu.agriculturefield.common.service.BaseService;
import com.omstu.agriculturefield.crop.dto.CropHistoryRequest;
import com.omstu.agriculturefield.crop.dto.CropHistoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/crop-histories")
@RequiredArgsConstructor
public class CropHistoryController {

    private final BaseService<CropHistoryRequest, CropHistoryResponse, Long> cropHistoryService;

    @GetMapping
    public List<CropHistoryResponse> getAllCropHistories() {
        return cropHistoryService.getAll();
    }

    @GetMapping("/{id}")
    public CropHistoryResponse getCropHistoryById(@PathVariable Long id) {
        return cropHistoryService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CropHistoryResponse createCropHistory(@Valid @RequestBody CropHistoryRequest request) {
        return cropHistoryService.create(request);
    }

    @PutMapping("/{id}")
    public CropHistoryResponse updateCropHistory(@PathVariable Long id, @Valid @RequestBody CropHistoryRequest request) {
        return cropHistoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCropHistory(@PathVariable Long id) {
        cropHistoryService.delete(id);
    }
}
