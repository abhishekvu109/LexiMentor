package com.abhi.leximentor.inventory.repository.inv;

import com.abhi.leximentor.inventory.entities.inv.Antonym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AntonymRepository extends JpaRepository<Antonym, Long> { }
