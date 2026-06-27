package com.abhi.leximentor.leximentor.log.mapping;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WordMapper implements RowMapper<WordRecord> {
    @Override
    public WordRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        return WordRecord.builder()
                .id(rs.getLong("id"))
                .refId(rs.getString("ref_id"))
                .word(rs.getString("word"))
                .status(rs.getInt("status"))
                .loadDate(rs.getTimestamp("load_date").toLocalDateTime())
                .jobId(rs.getLong("job_id"))
                .build();
    }
}
