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
                .refId(rs.getString("refId"))
                .status(rs.getString("status"))
                .loadDate(rs.getTimestamp("loadDate").toLocalDateTime())
                .jobId(rs.getLong("jobId"))
                .build();
    }
}
