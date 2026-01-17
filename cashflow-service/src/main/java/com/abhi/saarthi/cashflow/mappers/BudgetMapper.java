package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.dto.BudgetDTO;
import com.abhi.saarthi.cashflow.entities.Budget;
import com.abhi.saarthi.cashflow.util.KeyGeneratorUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = KeyGeneratorUtil.class, imports = KeyGeneratorUtil.class)
public interface BudgetMapper {

    @Mapping(target = "household.refId", source = "householdRefId")
    @Mapping(target = "category.refId", source = "categoryRefId")
    @Mapping(target = "uuid", expression = "java(KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId", expression = "java(KeyGeneratorUtil.refId())")
    Budget toEntity(BudgetDTO budgetDTO);

    @Mapping(target = "householdRefId", source = "household.refId")
    @Mapping(target = "categoryRefId", source = "category.refId")
    BudgetDTO toDto(Budget budget);

    void updateEntityFromDto(BudgetDTO budgetDTO, @MappingTarget Budget budget);
}
