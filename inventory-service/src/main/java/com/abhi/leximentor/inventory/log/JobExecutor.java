package com.abhi.leximentor.inventory.log;

import com.abhi.leximentor.inventory.service.inv.WordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class JobExecutor {

    private WordService wordService;

    private final ObjectMapper mapper = new ObjectMapper();

    private LoadLoggingService loadLoggingService;

    private JobService jobService;
    private ExecutorService executorService;

    private String nltk;

    private String datamuse;

    public JobExecutor(int threadPoolSize, WordService wordService, LoadLoggingService loadLoggingService, JobService jobService) {
        log.info("The new thread pool is initiated");
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.wordService = wordService;
        this.loadLoggingService = loadLoggingService;
        this.jobService = jobService;
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(new FileUrlResource("application.properties"));
            this.nltk = properties.getProperty("nltk");
            this.datamuse = properties.getProperty("datamuse");
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    public String submitJobs(Map<Long, String> words, long jobId) {
        log.info("Total words received: {}", words.size());
        for (Map.Entry<Long, String> word : words.entrySet()) {
            executorService.submit(JobRunner.builder()
                    .taskId(UUID.randomUUID().toString())
                    .datamuse(datamuse)
                    .nltk(nltk)
                    .objectMapper(mapper)
                    .wordService(wordService)
                    .word(word.getValue())
                    .wordId(word.getKey())
                    .loadLoggingService(loadLoggingService)
                    .build());
        }
        executorService.shutdown();
        jobService.updateStatus(jobId, 1);
        return "Started";
    }
}
