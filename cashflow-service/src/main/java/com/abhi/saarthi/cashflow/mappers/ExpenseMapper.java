package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.dto.ExpenseDTO;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.util.KeyGeneratorUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = KeyGeneratorUtil.class, imports = KeyGeneratorUtil.class)
public interface ExpenseMapper {

    @Mapping(target = "household.refId", source = "householdRefId")
    @Mapping(target = "category.refId", source = "categoryRefId")
    @Mapping(target = "uuid", expression = "java(KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId", expression = "java(KeyGeneratorUtil.refId())")
    Expense toEntity(ExpenseDTO expenseDTO);

    @Mapping(target = "householdRefId", source = "household.refId")
    @Mapping(target = "categoryRefId", source = "category.refId")
    ExpenseDTO toDto(Expense expense);

    void updateEntityFromDto(ExpenseDTO expenseDTO, @MappingTarget Expense expense);
}
