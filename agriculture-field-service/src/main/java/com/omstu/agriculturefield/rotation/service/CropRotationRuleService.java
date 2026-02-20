package com.omstu.agriculturefield.rotation.service;

import com.omstu.agriculturefield.common.service.BaseService;
import com.omstu.agriculturefield.rotation.dto.CropRotationRuleRequest;
import com.omstu.agriculturefield.rotation.dto.CropRotationRuleResponse;

import java.util.List;

public interface CropRotationRuleService extends BaseService<CropRotationRuleRequest, CropRotationRuleResponse, Long> {

    List<CropRotationRuleResponse> findByPredecessorCropId(Long predecessorCropId);

    List<CropRotationRuleResponse> findAllowedByPredecessorCropId(Long predecessorCropId);
}
