package com.abhi.saarthi.cashflow.repository;

import com.abhi.saarthi.cashflow.entities.HouseholdMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long>, JpaSpecificationExecutor<HouseholdMember> {

    Optional<HouseholdMember> findByRefId(long refId);

    Optional<HouseholdMember> findByUuid(String uuid);

}
