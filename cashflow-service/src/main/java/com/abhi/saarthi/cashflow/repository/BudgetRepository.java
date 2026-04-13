package com.abhi.saarthi.cashflow.repository;

import com.abhi.saarthi.cashflow.entities.Budget;
import com.abhi.saarthi.cashflow.entities.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long>, JpaSpecificationExecutor<Budget> {
    Optional<Budget> findByRefId(long refId);

    List<Budget> findByHousehold(Household household);
}
