package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.dto.HouseholdMemberDTO;
import com.abhi.saarthi.cashflow.model.HouseholdMemberSearchFilter;

import java.util.List;

public interface HouseholdMemberService {
    List<HouseholdMemberDTO> add(List<HouseholdMemberDTO> dtos);

    HouseholdMemberDTO get(long refId);

    HouseholdMemberDTO get(String uuid);

    List<HouseholdMemberDTO> update(List<HouseholdMemberDTO> dtos);

    void delete(List<HouseholdMemberDTO> dtos);
    void delete(long refId);

    List<HouseholdMemberDTO> search(HouseholdMemberSearchFilter filter);

    HouseholdMemberDTO findByRefId(long refId);
}
