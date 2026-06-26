package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.dto.ExpenseDTO;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.exceptions.entities.ServerException;
import com.abhi.saarthi.cashflow.mappers.ExpenseMapper;
import com.abhi.saarthi.cashflow.model.ExpenseSearchFilter;
import com.abhi.saarthi.cashflow.repository.CategoryRepository;
import com.abhi.saarthi.cashflow.repository.ExpenseRepository;
import com.abhi.saarthi.cashflow.repository.HouseholdRepository;
import com.abhi.saarthi.cashflow.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final HouseholdRepository householdRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    @Transactional
    public List<ExpenseDTO> add(List<ExpenseDTO> dtoList) {
        log.info("Adding new expenses: {}", dtoList);
        List<Expense> expenses = dtoList.stream().map(dto -> {
            Expense expense = expenseMapper.toEntity(dto);
            expense.setHousehold(householdRepository.findByRefId(Long.parseLong(dto.getHouseholdRefId()))
                    .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object household not found for refId : %s", dto.getHouseholdRefId()))));
            if (StringUtils.isNotEmpty(dto.getCategoryRefId())) {
                expense.setCategory(categoryRepository.findByRefId(Long.parseLong(dto.getCategoryRefId()))
                        .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object category not found for refId : %s", dto.getCategoryRefId()))));
            }
            return expense;
        }).toList();
        expenses = expenseRepository.saveAll(expenses);
        log.info("Successfully added new expenses: {}", expenses);
        return expenses.stream().map(expenseMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ExpenseDTO> update(List<ExpenseDTO> dtoList) {
        log.info("Updating expenses: {}", dtoList);
        List<Expense> expenses = dtoList.stream().map(dto -> {
            Expense expense = expenseRepository.findByRefId(Long.parseLong(dto.getRefId()))
                    .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object expense not found for refId : %s", dto.getRefId())));
            expenseMapper.updateEntityFromDto(dto, expense);
            expense.getItems().clear();
            if (dto.getItems() != null) expense.getItems().addAll(dto.getItems());
            expense.setHousehold(householdRepository.findByRefId(Long.parseLong(dto.getHouseholdRefId()))
                    .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object household not found for refId : %s", dto.getHouseholdRefId()))));
            if (StringUtils.isNotEmpty(dto.getCategoryRefId())) {
                expense.setCategory(categoryRepository.findByRefId(Long.parseLong(dto.getCategoryRefId()))
                        .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object category not found for refId : %s", dto.getCategoryRefId()))));
            }
            return expense;
        }).toList();
        expenses = expenseRepository.saveAll(expenses);
        log.info("Successfully updated expenses: {}", expenses);
        return expenses.stream().map(expenseMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(List<ExpenseDTO> dtoList) {
        log.info("Deleting expenses: {}", dtoList);
        List<Expense> expenses = dtoList.stream().map(dto -> expenseRepository.findByRefId(Long.parseLong(dto.getRefId()))
                .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object expense not found for refId : %s", dto.getRefId())))
        ).toList();
        expenseRepository.deleteAll(expenses);
        log.info("Successfully deleted expenses");
    }

    @Override
    public ExpenseDTO findByRefId(long refId) {
        log.info("Finding expense by refId: {}", refId);
        Expense expense = expenseRepository.findByRefId(refId)
                .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object expense not found for refId : %s", refId)));
        log.info("Found expense: {}", expense);
        return expenseMapper.toDto(expense);
    }

    @Override
    public List<ExpenseDTO> search(ExpenseSearchFilter filter) {
        log.info("Searching for expenses with filter: {}", filter);
        Specification<Expense> spec = Specification.unrestricted();
        if (StringUtils.isNotEmpty(filter.getRefId())) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("refId"), Long.parseLong(filter.getRefId())));
        }
        if (StringUtils.isNotEmpty(filter.getUuid())) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("uuid"), filter.getUuid()));
        }
        if (StringUtils.isNotEmpty(filter.getHouseholdRefId())) {
            spec = spec.and((root, query, cb) -> cb.equal(root.join("household").get("refId"), Long.parseLong(filter.getHouseholdRefId())));
        }
        if (StringUtils.isNotEmpty(filter.getOwner())) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("owner"), filter.getOwner()));
        }
        if (filter.getAmountFrom() > 0) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), filter.getAmountFrom()));
        }
        if (filter.getAmountTo() > 0) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("amount"), filter.getAmountTo()));
        }
        if (filter.getExpenseDateFrom() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("expenseDate"), filter.getExpenseDateFrom()));
        }
        if (filter.getExpenseDateTo() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("expenseDate"), filter.getExpenseDateTo()));
        }
        if (StringUtils.isNotEmpty(filter.getCategoryRefId())) {
            spec = spec.and((root, query, cb) -> cb.equal(root.join("category").get("refId"), Long.parseLong(filter.getCategoryRefId())));
        }
        if (StringUtils.isNotEmpty(filter.getExpenseType())) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), filter.getExpenseType()));
        }
        if (!StringUtils.isAnyEmpty(filter.getSortDir(), filter.getSortBy())) {
            Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
            List<ExpenseDTO> expenses = expenseRepository.findAll(spec, sort).stream()
                    .map(expenseMapper::toDto).toList();
            log.info("Found {} expenses", expenses.size());
            return expenses;
        }
        List<ExpenseDTO> expenses = expenseRepository.findAll(spec).stream()
                .map(expenseMapper::toDto).toList();
        log.info("Found {} expenses", expenses.size());
        return expenses;
    }
}

