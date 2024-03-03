package com.abhi.leximentor.inventory.log.mapping;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WordMapper implements RowMapper<WordRecord> {
    @Override
    public WordRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        return WordRecord.builder()
                .word(rs.getString("word"))
                .id(rs.getLong("id"))
                .refId(rs.getString("ref_id"))
                .status(rs.getInt("status"))
                .loadDate(rs.getTimestamp("load_date").toLocalDateTime())
                .jobId(rs.getLong("job_id"))
                .build();
    }
}
