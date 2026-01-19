package com.abhi.saarthi.cashflow.repository;

import com.abhi.saarthi.cashflow.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByRefId(long refId);

    Category findByNameIgnoreCase(String name);
}
