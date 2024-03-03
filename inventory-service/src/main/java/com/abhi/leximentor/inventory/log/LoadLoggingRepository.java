package com.abhi.leximentor.inventory.log;

import com.abhi.leximentor.inventory.log.mapping.WordMapper;
import com.abhi.leximentor.inventory.log.mapping.WordRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoadLoggingRepository {
    private final JdbcTemplate jdbcTemplate;

    public long addLog(WordRecord record) {
        String sql = "INSERT INTO word_record (refId,word,status,loadDate,jobId) values(?,?,?,?,?)";
        return jdbcTemplate.update(sql, record.getRefId(), record.getWord(), record.getStatus(), record.getLoadDate(), record.getJobId());
    }


    public List<WordRecord> getByDate(LocalDateTime localDateTime) {
        String sql = "select * from word_record where loadDate=?";
        return jdbcTemplate.query(sql, new Object[]{localDateTime}, new WordMapper());
    }

    public List<WordRecord> getByJobId(long jobId) {
        String sql = "select * from word_record where jobId=?";
        return jdbcTemplate.query(sql, new Object[]{jobId}, new WordMapper());
    }
}
