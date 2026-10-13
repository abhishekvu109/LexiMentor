package com.abhi.saarthi.cashflow.repository;

import com.abhi.saarthi.cashflow.entities.Earning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EarningRepository extends JpaRepository<Earning, Long>, JpaSpecificationExecutor<Earning> {

    Optional<Earning> findByRefId(long refId);

    Optional<Earning> findByUuid(String uuid);

    List<Earning> findByUsernameEqualsIgnoreCase(String username);

}
