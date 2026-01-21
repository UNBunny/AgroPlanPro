package com.omstu.agriculturefield.disease.controller;

import com.omstu.agriculturefield.common.service.BaseService;
import com.omstu.agriculturefield.disease.dto.DiseaseResistanceRequest;
import com.omstu.agriculturefield.disease.dto.DiseaseResistanceResponse;
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
@RequestMapping("/api/disease-resistances")
@RequiredArgsConstructor
public class DiseaseResistanceController {

    private final BaseService<DiseaseResistanceRequest, DiseaseResistanceResponse, Long> diseaseResistanceService;

    @GetMapping
    public List<DiseaseResistanceResponse> getAllDiseaseResistances() {
        return diseaseResistanceService.getAll();
    }

    @GetMapping("/{id}")
    public DiseaseResistanceResponse getDiseaseResistanceById(@PathVariable Long id) {
        return diseaseResistanceService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiseaseResistanceResponse createDiseaseResistance(@Valid @RequestBody DiseaseResistanceRequest request) {
        return diseaseResistanceService.create(request);
    }

    @PutMapping("/{id}")
    public DiseaseResistanceResponse updateDiseaseResistance(@PathVariable Long id, @Valid @RequestBody DiseaseResistanceRequest request) {
        return diseaseResistanceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDiseaseResistance(@PathVariable Long id) {
        diseaseResistanceService.delete(id);
    }
}
