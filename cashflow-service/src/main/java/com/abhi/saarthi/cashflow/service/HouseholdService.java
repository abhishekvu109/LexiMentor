package com.abhi.saarthi.cashflow.service;

import com.abhi.saarthi.cashflow.dto.HouseholdDTO;
import com.abhi.saarthi.cashflow.dto.dashboard.household.HouseholdOverviewDTO;
import com.abhi.saarthi.cashflow.model.HouseholdSearchFilter;

import java.util.List;

public interface HouseholdService {

    List<HouseholdDTO> add(List<HouseholdDTO> householdDTOS);

    List<HouseholdDTO> update(List<HouseholdDTO> householdDTOS);

    void delete(List<HouseholdDTO> householdDTOS);

    HouseholdDTO findByRefId(long refId);

    List<HouseholdDTO> search(HouseholdSearchFilter filter);

    HouseholdOverviewDTO buildHouseholdOverview(String username, long householdRefId);

}
