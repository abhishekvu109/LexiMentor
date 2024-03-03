package com.abhi.leximentor.inventory.log;

import com.abhi.leximentor.inventory.log.mapping.JobDTO;
import com.abhi.leximentor.inventory.log.mapping.JobMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class JobRepository {
    @Autowired
    @Qualifier(value = "jdbcTemplate1")
    private JdbcTemplate jdbcTemplate;

    public long createJob(JobDTO job) {
        String sql = "INSERT INTO job (refId,crtnDate,status) values(?,?,?)";
        return jdbcTemplate.update(sql, job.getRefId(), job.getCrtnDate(), job.getStatus());
    }

    public JobDTO getJob(long jobId) {
        String sql = "select * from job where jobId=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{jobId}, new JobMapper());
    }
}
