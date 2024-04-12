package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
}
