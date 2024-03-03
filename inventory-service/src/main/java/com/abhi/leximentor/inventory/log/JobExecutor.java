package com.abhi.leximentor.inventory.log;

import com.abhi.leximentor.inventory.service.WordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class JobExecutor {

    private WordService wordService;

    private final ObjectMapper mapper = new ObjectMapper();
    private ExecutorService executorService;

    @Value("${nltk}")
    private String nltk;

    @Value("${datamuse}")
    private String datamuse;

    public JobExecutor(int threadPoolSize, WordService wordService) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.wordService = wordService;
    }

    public String submitJobs(List<String> words) {
        for (String word : words) {
            executorService.submit(JobRunner.builder()
                    .taskId(UUID.randomUUID().toString())
                    .datamuse(datamuse)
                    .nltk(nltk)
                    .objectMapper(mapper)
                    .wordService(wordService)
                    .word(word)
                    .build());
        }
        executorService.shutdown();
        return "Started";
    }
}
