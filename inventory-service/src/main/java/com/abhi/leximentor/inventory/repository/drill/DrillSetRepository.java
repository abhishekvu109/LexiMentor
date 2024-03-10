package com.abhi.leximentor.inventory.repository.drill;

import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrillSetRepository extends JpaRepository<DrillSet, Long> {
    public DrillSet findByRefId(long refId);
}
