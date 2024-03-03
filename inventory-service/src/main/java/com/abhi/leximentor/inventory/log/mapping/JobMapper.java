package com.abhi.leximentor.inventory.log.mapping;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JobMapper implements RowMapper<JobDTO> {
    @Override
    public JobDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return JobDTO.builder()
                .jobId(rs.getLong("jobId"))
                .crtnDate(rs.getTimestamp("crtnDate").toLocalDateTime())
                .status(rs.getString("status"))
                .refId(rs.getString("refId"))
                .build();
    }
}
