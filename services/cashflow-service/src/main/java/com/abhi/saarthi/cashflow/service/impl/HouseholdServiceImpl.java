package com.abhi.saarthi.cashflow.service.impl;

import com.abhi.saarthi.cashflow.constants.Currency;
import com.abhi.saarthi.cashflow.constants.Status;
import com.abhi.saarthi.cashflow.dto.HouseholdDTO;
import com.abhi.saarthi.cashflow.dto.dashboard.household.BudgetVsActual;
import com.abhi.saarthi.cashflow.dto.dashboard.household.HouseholdOverviewDTO;
import com.abhi.saarthi.cashflow.entities.Budget;
import com.abhi.saarthi.cashflow.entities.Deposit;
import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.entities.Household;
import com.abhi.saarthi.cashflow.exceptions.entities.ServerException;
import com.abhi.saarthi.cashflow.mappers.HouseholdMapper;
import com.abhi.saarthi.cashflow.model.HouseholdSearchFilter;
import com.abhi.saarthi.cashflow.repository.BudgetRepository;
import com.abhi.saarthi.cashflow.repository.DepositRepository;
import com.abhi.saarthi.cashflow.repository.ExpenseRepository;
import com.abhi.saarthi.cashflow.repository.HouseholdRepository;
import com.abhi.saarthi.cashflow.service.HouseholdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HouseholdServiceImpl implements HouseholdService {
    private final HouseholdRepository householdRepository;
    private final HouseholdMapper householdMapper;
    private final DepositRepository depositRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    private static Specification<Household> withRefId(String refId) {
        if (StringUtils.isNotEmpty(refId)) {
            return (root, query, cb) -> cb.equal(root.get("refId"), refId);
        }
        return null;
    }

    private static Specification<Household> withName(String name) {
        if (StringUtils.isNotEmpty(name)) {
            return (root, query, cb) -> cb.like(cb.upper(root.get("name")), "%" + name.toUpperCase() + "%s");
        }
        return null;
    }

    private static Specification<Household> withCurrency(String currency) {
        if (StringUtils.isNotEmpty(currency)) {
            return (root, query, cb) -> cb.equal(root.get("currency"), Currency.parse(currency));
        }
        return null;
    }

    private static Specification<Household> withStatus(String status) {
        if (StringUtils.isNotEmpty(status)) {
            int statusInt = Status.ApplicationStatus.getStatus(status);
            return (root, query, cb) -> cb.equal(root.get("status"), statusInt);
        }
        return null;
    }

    private static Specification<Household> withUUID(String uuid) {
        if (StringUtils.isNotEmpty(uuid)) {
            return (root, query, cb) -> cb.equal(root.get("uuid"), uuid);
        }
        return null;
    }

    @Override
    @Transactional
    public List<HouseholdDTO> add(List<HouseholdDTO> householdDTOS) {
        log.info("Adding new households: {}", householdDTOS);
        List<Household> households = householdDTOS.stream().map(householdMapper::toEntity).peek(household -> {
            if (household.getMembers() != null) {
                household.getMembers().forEach(member -> member.setHousehold(household));
            }
        }).toList();
        households = householdRepository.saveAll(households);
        log.info("Successfully added new households: {}", households);
        return households.stream().map(householdMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<HouseholdDTO> update(List<HouseholdDTO> householdDTOS) {
        log.info("Updating households: {}", householdDTOS);
        List<Household> households = householdDTOS.stream().map(dto -> {
            Household household = householdRepository.findByRefId(Long.parseLong(dto.getRefId())).orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object household not found for refId : %s", dto.getRefId())));
            householdMapper.updateEntityFromDto(dto, household);
            return household;
        }).toList();
        households = householdRepository.saveAll(households);
        log.info("Successfully updated households: {}", households);
        return households.stream().map(householdMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(List<HouseholdDTO> householdDTOS) {
        log.info("Deleting households: {}", householdDTOS);
        List<Household> households = householdDTOS.stream().map(dto -> householdRepository.findByRefId(Long.parseLong(dto.getRefId())).orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object household not found for refId : %s", dto.getRefId())))).toList();
        householdRepository.deleteAll(households);
        log.info("Successfully deleted households");
    }

    @Override
    @Transactional(readOnly = true)
    public List<HouseholdDTO> search(HouseholdSearchFilter filter) {
        log.info("Searching for households with filter: {}", filter);
        if (filter == null || filter.isEmpty()) {
            HouseholdSearchFilter defaultFilter = HouseholdSearchFilter.defaultFilter();
            Sort sort = Sort.by(Sort.Direction.fromString(defaultFilter.getSortDir()), defaultFilter.getSortBy());
            List<HouseholdDTO> households = householdRepository.findAll(sort).stream().map(householdMapper::toDto).toList();
            log.info("Found {} households", households.size());
            return households;
        }
        Specification<Household> specification = Specification.unrestricted();
        specification = (StringUtils.isNotEmpty(filter.getUser())) ? ((root, query, cb) -> cb.equal(root.get("members").get("user"), filter.getUser())) : specification;
        specification = specification.and(withName(filter.getName()));
        specification = specification.and(withCurrency(filter.getCurrency()));
        specification = specification.and(withStatus(filter.getStatus()));
        specification = specification.and(withUUID(filter.getUuid()));
        specification = specification.and(withRefId(filter.getRefId()));
        Sort sort = StringUtils.isAnyEmpty(filter.getSortBy(), filter.getSortDir()) ? Sort.by(Sort.Direction.fromString("asc"), "name") : Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
        List<HouseholdDTO> households = householdRepository.findAll(specification, sort).stream().map(householdMapper::toDto).toList();
        log.info("Found {} households", households.size());
        return households;
    }

    @Override
    @Transactional(readOnly = true)
    public HouseholdDTO findByRefId(long refId) {
        log.info("Finding household by refId: {}", refId);
        Household household = householdRepository.findByRefId(refId).orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Entity object household not found for refId : %s", refId)));
        log.info("Found household: {}", household);
        return householdMapper.toDto(household);
    }

    @Override
    public HouseholdOverviewDTO buildHouseholdOverview(String username, long householdRefId) {
        Household household = householdRepository.findByRefId(householdRefId).orElseThrow(() -> new ServerException.EntityObjectNotFound(String.format("Household is not found: %s", householdRefId)));
        List<Deposit> deposits = depositRepository.findByHousehold(household);
        List<Expense> expenses = expenseRepository.findByHousehold(household);
        List<Budget> budgets = budgetRepository.findByHousehold(household);

        double totalDeposits = deposits.stream().mapToDouble(Deposit::getAmount).sum();
        double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double budgetCurrentMonth = budgets.stream().filter(budget -> budget.getBudgetDate().getMonth() == LocalDate.now().getMonth() && budget.getBudgetDate().getYear() == LocalDate.now().getYear()).mapToDouble(Budget::getAmount).sum();
        double availableBalance = totalDeposits - totalExpenses;
        double totalSpentCurrentMonth = expenses.stream().filter(expense -> (expense.getExpenseDate().getMonth() == LocalDate.now().getMonth()) && (expense.getExpenseDate().getYear() == LocalDate.now().getYear())).mapToDouble(Expense::getAmount).sum();
        double budgetLeftCurrentMonth = budgetCurrentMonth - totalSpentCurrentMonth;
        Map<String, Double> budgetSumByCategory = budgets.stream().filter(budget -> budget.getCategory() != null && budget.getBudgetDate().getMonth() == LocalDate.now().getMonth() && budget.getBudgetDate().getYear() == LocalDate.now().getYear())
                .collect(Collectors.groupingBy(budget -> budget.getCategory().getName(), Collectors.summingDouble(Budget::getAmount)));
        Map<String, Double> expenseSumByCategory = expenses.stream().filter(expense -> expense.getCategory() != null && expense.getExpenseDate().getMonth() == LocalDate.now().getMonth() && expense.getExpenseDate().getYear() == LocalDate.now().getYear())
                .collect(Collectors.groupingBy(expense -> expense.getCategory().getName(), Collectors.summingDouble(Expense::getAmount)));
        Map<String, BudgetVsActual> budgetVsActual = new LinkedHashMap<>();
        budgetSumByCategory.forEach((key, value) -> {
            double expenseValue = expenseSumByCategory.containsKey(key) ? expenseSumByCategory.get(key) : 0;
            budgetVsActual.put(key, BudgetVsActual.builder().budget(value).actual(expenseValue).build());
        });
        return HouseholdOverviewDTO.builder()
                .availableBalance(availableBalance)
                .totalSpent(totalSpentCurrentMonth)
                .budgetLeft(budgetLeftCurrentMonth)
                .budgetVsActual(budgetVsActual)
                .spendingSplit(expenseSumByCategory)
                .build();
    }
}
