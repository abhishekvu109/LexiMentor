package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.Excercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcerciseRepository extends JpaRepository<Excercise,Long> {
}
