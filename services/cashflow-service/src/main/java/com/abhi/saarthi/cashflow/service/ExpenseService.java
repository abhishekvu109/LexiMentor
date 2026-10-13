package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.dto.ExpenseDTO;
import com.abhi.saarthi.cashflow.model.ExpenseSearchFilter;

import java.util.List;

public interface ExpenseService {

    List<ExpenseDTO> add(List<ExpenseDTO> dtoList);

    List<ExpenseDTO> update(List<ExpenseDTO> dtoList);

    void delete(List<ExpenseDTO> dtoList);

    ExpenseDTO findByRefId(long refId);

    List<ExpenseDTO> search(ExpenseSearchFilter filter);

}
