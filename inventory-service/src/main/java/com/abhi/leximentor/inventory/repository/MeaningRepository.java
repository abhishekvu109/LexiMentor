package com.abhi.leximentor.inventory.repository;

import com.abhi.leximentor.inventory.entities.Meaning;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeaningRepository extends JpaRepository<Meaning, Long> {
}
