package com.abhi.writewise.inventory.repository.sql.mysql;

import com.abhi.writewise.inventory.entities.sql.mysql.WritingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WritingSessionRepository extends JpaRepository<WritingSession, Long> {
    WritingSession findByRefId(long refId);
}
