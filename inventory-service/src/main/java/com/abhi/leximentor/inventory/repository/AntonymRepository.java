package com.abhi.leximentor.inventory.repository;

import com.abhi.leximentor.inventory.entities.Antonym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AntonymRepository extends JpaRepository<Antonym, Long> { }
