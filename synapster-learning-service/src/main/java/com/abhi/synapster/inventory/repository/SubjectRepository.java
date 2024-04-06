package com.abhi.synapster.inventory.repository;

import com.abhi.synapster.inventory.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    public Subject findByRefId(long refId);
}
