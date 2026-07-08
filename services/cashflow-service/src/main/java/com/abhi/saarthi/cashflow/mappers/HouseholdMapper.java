package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.dto.HouseholdDTO;
import com.abhi.saarthi.cashflow.entities.Household;
import com.abhi.saarthi.cashflow.util.KeyGeneratorUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {KeyGeneratorUtil.class, HouseholdMemberMapper.class, BudgetMapper.class, ExpenseMapper.class, DepositMapper.class}, imports = {KeyGeneratorUtil.class})
public interface HouseholdMapper {
    @Mapping(target = "uuid", expression = "java(KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId", expression = "java(KeyGeneratorUtil.refId())")
    @Mapping(target = "currency", expression = "java(Currency.parse(householdDTO.getCurrency()))")
    @Mapping(target = "status", constant = "1")
    @Mapping(target = "members", source = "householdDTO.members")
    @Mapping(target = "budgets", source = "householdDTO.budgets")
    @Mapping(target = "expenses", source = "householdDTO.expenses")
    @Mapping(target = "deposits", source = "householdDTO.deposits")
    Household toEntity(HouseholdDTO householdDTO);


    @Mapping(target = "members", source = "household.members")
    @Mapping(target = "budgets", source = "household.budgets")
    @Mapping(target = "expenses", source = "household.expenses")
    @Mapping(target = "deposits", source = "household.deposits")
    @Mapping(target = "currency", expression = "java(Currency.parse(household.getCurrency()))")
    @Mapping(target = "status", expression = "java(com.abhi.saarthi.cashflow.constants.Status.ApplicationStatus.getStatusStr(household.getStatus()))")
    HouseholdDTO toDto(Household household);

    void updateEntityFromDto(HouseholdDTO householdDTO, @MappingTarget Household household);
}
