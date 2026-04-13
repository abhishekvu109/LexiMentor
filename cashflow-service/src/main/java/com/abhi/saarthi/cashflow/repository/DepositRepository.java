package com.abhi.saarthi.cashflow.repository;

import com.abhi.saarthi.cashflow.entities.Deposit;
import com.abhi.saarthi.cashflow.entities.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long>, JpaSpecificationExecutor<Deposit> {
    Optional<Deposit> findByRefId(long refId);

    List<Deposit> findByHousehold(Household household);
}
