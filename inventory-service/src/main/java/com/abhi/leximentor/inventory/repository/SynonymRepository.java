package com.abhi.leximentor.inventory.repository;

import com.abhi.leximentor.inventory.entities.Synonym;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SynonymRepository extends JpaRepository<Synonym, Long> {
}
