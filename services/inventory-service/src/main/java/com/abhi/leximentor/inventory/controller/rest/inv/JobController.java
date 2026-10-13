package com.abhi.leximentor.inventory.controller.rest.inv;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.dto.other.JobControllerDTO;
import com.abhi.leximentor.inventory.log.JobExecutor;
import com.abhi.leximentor.inventory.log.JobService;
import com.abhi.leximentor.inventory.log.LoadLoggingService;
import com.abhi.leximentor.inventory.log.mapping.JobDTO;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.service.inv.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JobController {
    private final JobService service;
    private final LoadLoggingService loadLoggingService;
    private final WordService wordService;

    @PostMapping(value = "/api/leximentor/jobs", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> createJob(@RequestBody JobControllerDTO dto) {
        log.info("Job create requested. wordsCount={}", dto == null || dto.getWords() == null ? 0 : dto.getWords().size());
        String refId = service.createJob();
        JobDTO jobDTO = service.getJobByRefId(refId);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            List<Long> listOfWords = new LinkedList<>();
            for (String word : dto.getWords())
                listOfWords.add(loadLoggingService.loadWord(word, jobDTO.getJobId()));
        });
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Job creation is in progress, JobId : " + jobDTO.getJobId());
    }

    @GetMapping(value = "/api/leximentor/jobs/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getJobStatus(@PathVariable long jobId) {
        log.info("Job status requested. jobId={}", jobId);
        JobDTO job = service.getJob(jobId);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, job);
    }

    @PostMapping(value = "/api/leximentor/jobs/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> executeJob(@PathVariable long jobId) {
        log.info("Job execute requested. jobId={}", jobId);
        Map<Long, String> words = loadLoggingService.getWordsByJobId(jobId);
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            JobExecutor jobExecutor = new JobExecutor(20, wordService);
//            jobExecutor.submitJobs(words);
//        }, Executors.newFixedThreadPool(1));
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            JobExecutor jobExecutor = new JobExecutor(20, wordService, loadLoggingService, service);
            jobExecutor.submitJobs(words, jobId);
        });
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Your Job is successfully started");
    }
}
