package com.omstu.agriculturefield.rotation.controller;

import com.omstu.agriculturefield.rotation.dto.CropRotationRuleRequest;
import com.omstu.agriculturefield.rotation.dto.CropRotationRuleResponse;
import com.omstu.agriculturefield.rotation.service.CropRotationRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rotation-rules")
@RequiredArgsConstructor
@Slf4j
public class CropRotationRuleController {

    private final CropRotationRuleService cropRotationRuleService;

    @GetMapping
    public List<CropRotationRuleResponse> getAllRules() {
        log.info("Fetching all crop rotation rules");
        return cropRotationRuleService.getAll();
    }

    @GetMapping("/{id}")
    public CropRotationRuleResponse getRuleById(@PathVariable Long id) {
        log.info("Fetching crop rotation rule with id: {}", id);
        return cropRotationRuleService.getById(id);
    }

    @GetMapping("/by-predecessor")
    public List<CropRotationRuleResponse> getRulesByPredecessor(
            @RequestParam Long predecessorCropId,
            @RequestParam(required = false, defaultValue = "false") Boolean allowedOnly
    ) {
        log.info("Fetching rotation rules for predecessor cropId: {}, allowedOnly: {}", predecessorCropId, allowedOnly);
        if (Boolean.TRUE.equals(allowedOnly)) {
            return cropRotationRuleService.findAllowedByPredecessorCropId(predecessorCropId);
        }
        return cropRotationRuleService.findByPredecessorCropId(predecessorCropId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CropRotationRuleResponse createRule(@Valid @RequestBody CropRotationRuleRequest request) {
        log.info("Creating crop rotation rule: predecessor={}, successor={}", request.predecessorCropId(), request.successorCropId());
        return cropRotationRuleService.create(request);
    }

    @PutMapping("/{id}")
    public CropRotationRuleResponse updateRule(
            @PathVariable Long id,
            @Valid @RequestBody CropRotationRuleRequest request
    ) {
        log.info("Updating crop rotation rule with id: {}", id);
        return cropRotationRuleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(@PathVariable Long id) {
        log.info("Deleting crop rotation rule with id: {}", id);
        cropRotationRuleService.delete(id);
    }
}
