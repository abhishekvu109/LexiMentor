package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.dto.BudgetDTO;
import com.abhi.saarthi.cashflow.model.BudgetSearchFilter;

import java.util.List;

public interface BudgetService {
    List<BudgetDTO> add(List<BudgetDTO> dtoList);

    List<BudgetDTO> update(List<BudgetDTO> dtoList);

    void delete(List<BudgetDTO> dtoList);

    BudgetDTO findByRefId(long refId);

    List<BudgetDTO> search(BudgetSearchFilter filter);

}
