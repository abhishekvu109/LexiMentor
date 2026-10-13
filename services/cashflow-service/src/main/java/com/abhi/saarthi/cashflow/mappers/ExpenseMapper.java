package com.abhi.saarthi.cashflow.mappers;

import com.abhi.saarthi.cashflow.constants.ExpenseFor;
import com.abhi.saarthi.cashflow.constants.PaymentMode;
import com.abhi.saarthi.cashflow.dto.ExpenseDTO;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.util.KeyGeneratorUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {KeyGeneratorUtil.class, ExpenseFor.class, PaymentMode.class}, imports = {KeyGeneratorUtil.class, PaymentMode.class, ExpenseFor.class})
public interface ExpenseMapper {

    @Mapping(target = "household.refId", source = "householdRefId")
    @Mapping(target = "category.refId", source = "categoryRefId")
    @Mapping(target = "uuid", expression = "java(KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId", expression = "java(KeyGeneratorUtil.refId())")
    @Mapping(target = "expenseFor", expression = "java(ExpenseFor.parse(expenseDTO.getExpenseFor()))")
    @Mapping(target = "paymentMode", expression = "java(PaymentMode.of(expenseDTO.getPaymentMode()))")
    Expense toEntity(ExpenseDTO expenseDTO);

    @Mapping(target = "householdRefId", source = "household.refId")
    @Mapping(target = "categoryRefId", source = "category.refId")
    @Mapping(target = "expenseDate", source = "expenseDate")
    @Mapping(target = "expenseFor", expression = "java(ExpenseFor.parse(expense.getExpenseFor()))")
    @Mapping(target = "paymentMode", expression = "java(PaymentMode.of(expense.getPaymentMode()))")
    ExpenseDTO toDto(Expense expense);

    @Mapping(target = "expenseFor", expression = "java(ExpenseFor.parse(expenseDTO.getExpenseFor()))")
    @Mapping(target = "paymentMode", expression = "java(PaymentMode.of(expenseDTO.getPaymentMode()))")
    @Mapping(target = "items", ignore = true)
    void updateEntityFromDto(ExpenseDTO expenseDTO, @MappingTarget Expense expense);
}
