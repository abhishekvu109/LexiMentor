package com.abhi.leximentor.inventory.repository.inv;

import com.abhi.leximentor.inventory.entities.inv.Synonym;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SynonymRepository extends JpaRepository<Synonym, Long> {
}
