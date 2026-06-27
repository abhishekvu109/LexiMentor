package com.abhi.leximentor.leximentor.repository.drill;

import com.abhi.leximentor.leximentor.entities.drill.Drill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrillRepository extends JpaRepository<Drill, Long>, JpaSpecificationExecutor<Drill> {
    Optional<Drill> findByKey(String key);
}
