package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.dto.DepositDTO;
import com.abhi.saarthi.cashflow.model.DepositSearchFilter;

import java.util.List;

public interface DepositService {
    List<DepositDTO> add(List<DepositDTO> dtoList);

    List<DepositDTO> update(List<DepositDTO> dtoList);

    void delete(List<DepositDTO> dtoList);

    List<DepositDTO> search(DepositSearchFilter filter);

    DepositDTO findByRefId(long refId);
}
