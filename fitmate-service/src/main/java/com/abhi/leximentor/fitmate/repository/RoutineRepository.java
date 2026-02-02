package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long>, JpaSpecificationExecutor<Routine> {

    Stream<Routine> findAllBy();

    Routine findByRefId(long refId);

    List<Routine> findByRefIdIn(List<Long> refIds);

    List<Routine> findAllByOrderByRoutineDateDesc();
    List<Routine> findByUsernameOrderByRoutineDateDesc(String username);

    List<Routine> findByUsername(String username);

}
