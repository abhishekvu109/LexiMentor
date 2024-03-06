package com.abhi.leximentor.inventory.log;

import com.abhi.leximentor.inventory.log.mapping.JobDTO;
import com.abhi.leximentor.inventory.log.mapping.JobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JobRepository {
    private final JdbcTemplate jdbcTemplate;


    public String createJob(JobDTO job) {
        String sql = "INSERT INTO job (ref_id,crtn_date,status) values(?,?,?)";
        jdbcTemplate.update(sql, job.getRefId(), job.getCrtnDate(), job.getStatus());
        return job.getRefId();
    }

    public JobDTO getJob(long jobId) {
        String sql = "select * from job where job_id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{jobId}, new JobMapper());
    }

    public void updateJob(long jobId, int status) {
        String sql = "update job set status=? where job_id=?";
        jdbcTemplate.update(sql, status, jobId);
    }

    public JobDTO getJobByRefId(String refId) {
        String sql = "select * from job where ref_id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{refId}, new JobMapper());
    }
}
