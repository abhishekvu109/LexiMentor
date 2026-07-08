package com.abhi.saarthi.auth.repository;

import com.abhi.saarthi.auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByRefId(long refId);

    Optional<UserRole> findByNameIgnoreCase(String name);

    Optional<List<UserRole>> findByNameIn(Set<String> names);
}
