package com.abhi.leximentor.inventory.log;

import com.abhi.leximentor.inventory.log.mapping.WordMapper;
import com.abhi.leximentor.inventory.log.mapping.WordRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoadLoggingRepository {
    private final JdbcTemplate jdbcTemplate;

    public long addLog(WordRecord record) {
        String sql = "INSERT INTO word_record (ref_id,word,status,load_date,job_id) values(?,?,?,?,?)";
        return jdbcTemplate.update(sql, record.getRefId(), record.getWord(), record.getStatus(), record.getLoadDate(), record.getJobId());
    }

    public long updateLog(long id, int status) {
        String sql = "update word_record set status=? where id=?";
        return jdbcTemplate.update(sql, status, id);
    }


    public List<WordRecord> getByDate(LocalDateTime localDateTime) {
        String sql = "select * from word_record where load_date=?";
        return jdbcTemplate.query(sql, new Object[]{localDateTime}, new WordMapper());
    }

    public List<WordRecord> getByJobId(long jobId) {
        String sql = "select * from word_record where job_id=?";
        return jdbcTemplate.query(sql, new Object[]{jobId}, new WordMapper());
    }
}
