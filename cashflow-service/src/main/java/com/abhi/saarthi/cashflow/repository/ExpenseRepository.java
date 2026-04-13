package com.abhi.saarthi.cashflow.repository;

import com.abhi.saarthi.cashflow.entities.Expense;
import com.abhi.saarthi.cashflow.entities.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
    Optional<Expense> findByRefId(long refId);

    List<Expense> findByHouseholdAndOwner(Household household, String owner);

    List<Expense> findByOwnerIgnoreCase(String owner);

    List<Expense> findByHousehold(Household household);

    List<Expense> findByHouseholdRefIdAndExpenseDateBetween(long householdRefId, LocalDate from, LocalDate to);
}
