package com.abhi.saarthi.cashflow.service.util;

import com.abhi.saarthi.cashflow.constants.Currency;
import com.abhi.saarthi.cashflow.constants.Status;
import com.abhi.saarthi.cashflow.dto.*;
import com.abhi.saarthi.cashflow.entities.*;
import com.abhi.saarthi.cashflow.util.KeyGeneratorUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;

public class ServiceUtil {
    public static class HouseholdUtil {
        public static Household buildEntity(HouseholdDTO dto) {
            Household household = Household.builder()
                    .refId(KeyGeneratorUtil.refId())
                    .uuid(KeyGeneratorUtil.uuid())
                    .name(dto.getName())
                    .currency(Currency.parse(dto.getCurrency()))
                    .createdAt(LocalDateTime.now())
                    .status(Status.ApplicationStatus.getStatus(dto.getStatus()))
                    .build();
            if (CollectionUtils.isNotEmpty(dto.getMembers())) {
                household.setMembers(dto.getMembers().stream().map(HouseholdMemberUtil::buildEntity).toList());
            }
            if (CollectionUtils.isNotEmpty(dto.getExpenses())) {
                household.setExpenses(dto.getExpenses().stream().map(ExpenseUtil::buildEntity).toList());
            }
            if (CollectionUtils.isNotEmpty(dto.getBudgets())) {
                household.setBudgets(dto.getBudgets().stream().map(BudgetUtil::buildEntity).toList());
            }
            return household;
        }

        public static Household updateEntity(Household household, HouseholdDTO dto) {
            household.setName(dto.getName());
            household.setCurrency(Currency.parse(dto.getCurrency()));
            household.setStatus(Status.ApplicationStatus.getStatus(dto.getStatus()));
            if (CollectionUtils.isNotEmpty(dto.getMembers())) {
                household.setMembers(dto.getMembers().stream().map(HouseholdMemberUtil::buildEntity).toList());
            }
            if (CollectionUtils.isNotEmpty(dto.getExpenses())) {
                household.setExpenses(dto.getExpenses().stream().map(ExpenseUtil::buildEntity).toList());
            }
            if (CollectionUtils.isNotEmpty(dto.getBudgets())) {
                household.setBudgets(dto.getBudgets().stream().map(BudgetUtil::buildEntity).toList());
            }
            return household;
        }

        public static HouseholdDTO buildDTO(Household entity) {
            return HouseholdDTO.builder()
                    .uuid(entity.getUuid())
                    .refId(String.valueOf(entity.getRefId()))
                    .name(entity.getName())
                    .currency(Currency.parse(entity.getCurrency()))
                    .status(Status.ApplicationStatus.getStatusStr(entity.getStatus()))
                    .createdAt(entity.getCreatedAt())
                    .members((CollectionUtils.isNotEmpty(entity.getMembers()))
                            ? entity.getMembers().stream().map(ServiceUtil.HouseholdMemberUtil::buildDTO).toList()
                            : Collections.emptyList())
                    .build();
        }

        public static HouseholdDTO buildDTO(Household entity, boolean expensesRequired, boolean budgetsRequired) {
            return HouseholdDTO.builder()
                    .uuid(entity.getUuid())
                    .refId(String.valueOf(entity.getRefId()))
                    .name(entity.getName())
                    .currency(Currency.parse(entity.getCurrency()))
                    .status(Status.ApplicationStatus.getStatusStr(entity.getStatus()))
                    .createdAt(entity.getCreatedAt())
                    .members((CollectionUtils.isNotEmpty(entity.getMembers()))
                            ? entity.getMembers().stream().map(ServiceUtil.HouseholdMemberUtil::buildDTO).toList()
                            : Collections.emptyList())
                    .expenses((CollectionUtils.isNotEmpty(entity.getExpenses()))
                            ? entity.getExpenses().stream().map(ServiceUtil.ExpenseUtil::buildDTO).toList()
                            : Collections.emptyList())
                    .budgets((CollectionUtils.isNotEmpty(entity.getBudgets()))
                            ? entity.getBudgets().stream().map(ServiceUtil.BudgetUtil::buildDTO).toList()
                            : Collections.emptyList())
                    .build();
        }
    }

    public static class BudgetUtil {
        public static Budget buildEntity(BudgetDTO dto) {
            return Budget.builder()
                    .uuid(KeyGeneratorUtil.uuid())
                    .refId(KeyGeneratorUtil.refId())
                    .amount(dto.getAmount())
                    .period(dto.getPeriod())
                    .year(dto.getYear())
                    .month(dto.getMonth())
                    .status(Status.ApplicationStatus.getStatus(dto.getStatus()))
                    .build();
        }

        public static BudgetDTO buildDTO(Budget entity) {
            return BudgetDTO.builder()
                    .uuid(entity.getUuid())
                    .refId(String.valueOf(entity.getRefId()))
                    .amount(entity.getAmount())
                    .period(entity.getPeriod())
                    .year(entity.getYear())
                    .month(entity.getMonth())
                    .householdRefId(String.valueOf(entity.getHousehold().getRefId()))
                    .status(Status.ApplicationStatus.getStatusStr(entity.getStatus()))
                    .createdAt(entity.getCreatedAt())
                    .build();
        }

        public static Budget updateEntity(Budget entity, BudgetDTO dto) {
            entity.setAmount(dto.getAmount());
            entity.setPeriod(dto.getPeriod());
            entity.setYear(dto.getYear());
            entity.setMonth(dto.getMonth());
            entity.setStatus(Status.ApplicationStatus.getStatus(dto.getStatus()));
            return entity;
        }
    }

    public static class HouseholdMemberUtil {
        public static HouseholdMember buildEntity(HouseholdMemberDTO dto) {
            return HouseholdMember.builder()
                    .uuid(KeyGeneratorUtil.uuid())
                    .refId(KeyGeneratorUtil.refId())
                    .user(dto.getUser())
                    .role(dto.getRole())
                    .joiningDate(LocalDateTime.now())
                    .build();
        }

        public static HouseholdMemberDTO buildDTO(HouseholdMember entity) {
            return HouseholdMemberDTO.builder()
                    .uuid(entity.getUuid())
                    .refId(String.valueOf(entity.getRefId()))
                    .user(entity.getUser())
                    .householdRefId(String.valueOf(entity.getHousehold().getRefId()))
                    .status(Status.ApplicationStatus.getStatusStr(entity.getStatus()))
                    .joiningDate(entity.getJoiningDate())
                    .build();
        }

        public static HouseholdMember updateEntity(HouseholdMember entity, HouseholdMemberDTO dto) {
            entity.setUser(dto.getUser());
            entity.setRole(dto.getRole());
            entity.setStatus(Status.ApplicationStatus.getStatus(dto.getStatus()));
            return entity;
        }
    }

    public static class ExpenseUtil {
        public static Expense buildEntity(ExpenseDTO dto) {
            return Expense.builder()
                    .refId(KeyGeneratorUtil.refId())
                    .uuid(KeyGeneratorUtil.uuid())
                    .owner(dto.getOwner())
                    .amount(dto.getAmount())
                    .expenseDate(dto.getExpenseDate())
                    .description(dto.getDescription())
                    .type(dto.getType())
                    .build();
        }

        public static ExpenseDTO buildDTO(Expense entity) {
            return ExpenseDTO.builder()
                    .refId(String.valueOf(entity.getRefId()))
                    .uuid(entity.getUuid())
                    .householdRefId(String.valueOf(entity.getHousehold().getRefId()))
                    .categoryRefId(String.valueOf(entity.getCategory().getRefId()))
                    .owner(entity.getOwner())
                    .amount(entity.getAmount())
                    .expenseDate(entity.getExpenseDate())
                    .description(entity.getDescription())
                    .type(entity.getType())
                    .build();
        }

        public static Expense updateEntity(Expense entity, ExpenseDTO dto) {
            entity.setOwner(dto.getOwner());
            entity.setAmount(dto.getAmount());
            entity.setExpenseDate(dto.getExpenseDate());
            entity.setDescription(dto.getDescription());
            entity.setType(dto.getType());
            return entity;
        }
    }

    public static class CategoryUtil {
        public static Category buildEntity(CategoryDTO dto) {
            return Category.builder()
                    .refId(KeyGeneratorUtil.refId())
                    .uuid(KeyGeneratorUtil.uuid())
                    .name(dto.getName())
                    .status(dto.getStatus())
                    .build();
        }

        public static CategoryDTO buildDTO(Category entity) {
            return CategoryDTO.builder()
                    .refId(String.valueOf(entity.getRefId()))
                    .uuid(entity.getUuid())
                    .name(entity.getName())
                    .status(entity.getStatus())
                    .build();
        }

        public static Category updateEntity(Category entity, CategoryDTO dto) {
            entity.setName(dto.getName());
            entity.setStatus(dto.getStatus());
            return entity;
        }
    }
}
