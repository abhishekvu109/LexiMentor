package com.abhi.leximentor.inventory.service.inv;

import com.abhi.leximentor.inventory.dto.inv.EvaluatorDTO;

import java.util.List;

public interface EvaluatorService {
    public EvaluatorDTO add(EvaluatorDTO dto);

    public List<EvaluatorDTO> addAll(List<EvaluatorDTO> list);

    public EvaluatorDTO getByName(String name);

    public List<EvaluatorDTO> getByDrillType(String drillType);

    public EvaluatorDTO getByRefId(long refId);
}
