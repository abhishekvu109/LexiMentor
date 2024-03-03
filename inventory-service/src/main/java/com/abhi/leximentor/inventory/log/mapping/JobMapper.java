package com.abhi.leximentor.inventory.log.mapping;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JobMapper implements RowMapper<JobDTO> {
    @Override
    public JobDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return JobDTO.builder()
                .jobId(rs.getLong("job_id"))
                .crtnDate(rs.getTimestamp("crtn_date").toLocalDateTime())
                .status(rs.getInt("status"))
                .refId(rs.getString("ref_id"))
                .build();
    }
}
