package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.dto.BudgetDTO;
import com.abhi.saarthi.cashflow.entities.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "household.refId", source = "householdRefId")
    Budget toEntity(BudgetDTO budgetDTO);

    @Mapping(target = "householdRefId", source = "household.refId")
    BudgetDTO toDto(Budget budget);

    void updateEntityFromDto(BudgetDTO budgetDTO, @MappingTarget Budget budget);
}
