package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.constants.QueryConstants;
import com.abhi.leximentor.fitmate.entities.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    List<Routine> findByUsernameAndRoutineDateBetween(String username, LocalDate startDate, LocalDate endDate);

    @Query(value = QueryConstants.ROUTINE_DRILL_VIEW, nativeQuery = true)
    List<Object[]> findRoutineAndMergeColumnsByUsername(@Param("username") String username);
}
