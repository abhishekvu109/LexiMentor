package com.abhi.writewise.inventory.repository.sql.mysql;

import com.abhi.writewise.inventory.entities.sql.mysql.LLmTopicMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LlmTopicMasterRepository extends JpaRepository<LLmTopicMaster, Long> {
    LLmTopicMaster findByRefId(long refId);
}
