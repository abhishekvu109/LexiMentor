package com.abhi.saarthi.cashflow.repository;

import com.abhi.saarthi.cashflow.entities.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long>, JpaSpecificationExecutor<Household> {

    Optional<Household> findByRefId(long refId);
}
