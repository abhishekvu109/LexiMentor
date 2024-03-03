package com.abhi.leximentor.inventory.log;

import com.abhi.leximentor.inventory.log.mapping.WordRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoadLoggingService {
    private final LoadLoggingRepository repository;

    public long loadWord(String word, long jobId) {
        return repository.addLog(WordRecord.builder()
                .jobId(jobId)
                .loadDate(LocalDateTime.now())
                .word(word)
                .refId(UUID.randomUUID().toString())
                .status("LOADED")
                .build());
    }

    public List<String> getWordsByJobId(long jobId){
        List<WordRecord> wordRecords=repository.getByJobId(jobId);
        return wordRecords.stream().map(words-> words.getWord()).collect(Collectors.toList());
    }

}
