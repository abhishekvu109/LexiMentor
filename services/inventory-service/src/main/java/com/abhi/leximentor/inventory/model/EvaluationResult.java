package com.abhi.leximentor.inventory.model;

import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EvaluationResult {
    private boolean isSuccess;
    private List<DrillEvaluationDTO> result;
}
