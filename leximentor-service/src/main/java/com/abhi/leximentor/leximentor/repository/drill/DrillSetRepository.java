package com.abhi.leximentor.leximentor.repository.drill;

import com.abhi.leximentor.leximentor.entities.drill.Drill;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DrillSetRepository extends JpaRepository<DrillSet, Long> {
    Optional<DrillSet> findByKey(String key);

    List<DrillSet> findByDrill(Drill drill);
}
