package com.abhi.leximentor.inventory.log;

import com.abhi.leximentor.inventory.log.mapping.WordRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
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
                .status(0)
                .build());
    }

    public Map<Long, String> getWordsByJobId(long jobId) {
        return repository.getByJobId(jobId).stream().filter(wr -> wr.getStatus() == 0)
                .collect(Collectors.toMap(
                        WordRecord::getId,
                        WordRecord::getWord
                ));

    }

    public void updateStatus(long wordId, int status) {
        repository.updateLog(wordId, status);
    }


}
