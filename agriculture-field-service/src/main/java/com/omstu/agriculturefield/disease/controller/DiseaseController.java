package com.omstu.agriculturefield.disease.controller;

import com.omstu.agriculturefield.common.service.BaseService;
import com.omstu.agriculturefield.disease.dto.DiseaseRequest;
import com.omstu.agriculturefield.disease.dto.DiseaseResponse;
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
@RequestMapping("/api/diseases")
@RequiredArgsConstructor
public class DiseaseController {

    private final BaseService<DiseaseRequest, DiseaseResponse, Long> diseaseService;

    @GetMapping
    public List<DiseaseResponse> getAllDiseases() {
        return diseaseService.getAll();
    }

    @GetMapping("/{id}")
    public DiseaseResponse getDiseaseById(@PathVariable Long id) {
        return diseaseService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiseaseResponse createDisease(@Valid @RequestBody DiseaseRequest request) {
        return diseaseService.create(request);
    }

    @PutMapping("/{id}")
    public DiseaseResponse updateDisease(@PathVariable Long id, @Valid @RequestBody DiseaseRequest request) {
        return diseaseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDisease(@PathVariable Long id) {
        diseaseService.delete(id);
    }
}
