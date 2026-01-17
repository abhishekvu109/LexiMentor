package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.dto.BudgetDTO;
import com.abhi.saarthi.cashflow.entities.Budget;
import com.abhi.saarthi.cashflow.exceptions.entities.ServerException;
import com.abhi.saarthi.cashflow.mappers.BudgetMapper;
import com.abhi.saarthi.cashflow.model.BudgetSearchFilter;
import com.abhi.saarthi.cashflow.repository.BudgetRepository;
import com.abhi.saarthi.cashflow.repository.CategoryRepository;
import com.abhi.saarthi.cashflow.repository.HouseholdRepository;
import com.abhi.saarthi.cashflow.service.BudgetService;
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

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final HouseholdRepository householdRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetMapper budgetMapper;

    @Override
    @Transactional
    public List<BudgetDTO> add(List<BudgetDTO> dtoList) {
        log.info("Adding new budgets: {}", dtoList);
        List<Budget> budgets = dtoList.stream().map(dto -> {
            Budget budget = budgetMapper.toEntity(dto);
            budget.setHousehold(householdRepository.findByRefId(Long.parseLong(dto.getHouseholdRefId()))
                    .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object household not found for refId : %s", dto.getHouseholdRefId()))));
            budget.setCategory(categoryRepository.findByRefId(Long.parseLong(dto.getCategoryRefId()))
                    .orElse(categoryRepository.findByNameIgnoreCase("OTHERS")));
            return budget;
        }).toList();
        budgets = budgetRepository.saveAll(budgets);
        log.info("Successfully added new budgets: {}", budgets);
        return budgets.stream().map(budgetMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BudgetDTO> update(List<BudgetDTO> dtoList) {
        log.info("Updating budgets: {}", dtoList);
        List<Budget> budgets = dtoList.stream().map(dto -> {
            Budget budget = budgetRepository.findByRefId(Long.parseLong(dto.getRefId()))
                    .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object budget not found for refId : %s", dto.getRefId())));
            budgetMapper.updateEntityFromDto(dto, budget);
            budget.setHousehold(householdRepository.findByRefId(Long.parseLong(dto.getHouseholdRefId()))
                    .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object household not found for refId : %s", dto.getHouseholdRefId()))));
            budget.setCategory(categoryRepository.findByRefId(Long.parseLong(dto.getCategoryRefId()))
                    .orElse(categoryRepository.findByNameIgnoreCase("OTHERS")));
            return budget;
        }).toList();
        budgets = budgetRepository.saveAll(budgets);
        log.info("Successfully updated budgets: {}", budgets);
        return budgets.stream().map(budgetMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(List<BudgetDTO> dtoList) {
        log.info("Deleting budgets: {}", dtoList);
        List<Budget> budgets = dtoList.stream().map(dto -> budgetRepository.findByRefId(Long.parseLong(dto.getRefId()))
                .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object budget not found for refId : %s", dto.getRefId())))
        ).toList();
        budgetRepository.deleteAll(budgets);
        log.info("Successfully deleted budgets");
    }

    @Override
    public BudgetDTO findByRefId(long refId) {
        log.info("Finding budget by refId: {}", refId);
        Budget budget = budgetRepository.findByRefId(refId)
                .orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object budget not found for refId : %s", refId)));
        log.info("Found budget: {}", budget);
        return budgetMapper.toDto(budget);
    }

    @Override
    public List<BudgetDTO> search(BudgetSearchFilter filter) {
        log.info("Searching for budgets with filter: {}", filter);
        Specification<Budget> spec = Specification.unrestricted();
        spec = (StringUtils.isNotEmpty(filter.getUuid())) ? spec.and((root, query, cb) -> cb.equal(root.get("uuid"), filter.getUuid())) : spec;
        spec = (StringUtils.isNotEmpty(filter.getRefId())) ? spec.and((root, query, cb) -> cb.equal(root.get("refId"), Long.parseLong(filter.getRefId()))) : spec;
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
        List<BudgetDTO> budgets = budgetRepository.findAll(spec, sort).stream()
                .map(budgetMapper::toDto).toList();
        log.info("Found {} budgets", budgets.size());
        return budgets;
    }
}
