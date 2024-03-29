package com.abhi.leximentor.inventory.repository.inv;

import com.abhi.leximentor.inventory.entities.inv.Evaluator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluatorRepository extends JpaRepository<Evaluator, Long> {
    public Evaluator findByName(String name);

    public Evaluator findByNameAndDrillType(String name, String drillType);

    public Evaluator findByRefId(long refId);

    public List<Evaluator> findByDrillType(String drillType);
}
