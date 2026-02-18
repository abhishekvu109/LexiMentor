package com.abhi.leximentor.leximentor.service.inv;

import com.abhi.leximentor.leximentor.dto.inv.EvaluatorDTO;

import java.util.List;

public interface EvaluatorService {
    public EvaluatorDTO add(EvaluatorDTO dto);

    public List<EvaluatorDTO> addAll(List<EvaluatorDTO> list);

    public EvaluatorDTO getByName(String name);

    public List<EvaluatorDTO> getByDrillType(String drillType);

    public EvaluatorDTO getByRefId(long refId);
}
