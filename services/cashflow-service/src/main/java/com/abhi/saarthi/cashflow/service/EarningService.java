package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.dto.EarningDTO;
import com.abhi.saarthi.cashflow.model.EarningSearchFilter;

import java.util.List;

public interface EarningService {
    List<EarningDTO> add(List<EarningDTO> dtoList);

    List<EarningDTO> update(List<EarningDTO> dtoList);

    void delete(List<EarningDTO> dtoList);

    List<EarningDTO> search(EarningSearchFilter filter);

    EarningDTO findByRefId(long refId);
}
