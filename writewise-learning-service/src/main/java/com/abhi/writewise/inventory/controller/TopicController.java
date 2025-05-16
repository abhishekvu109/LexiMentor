package com.abhi.writewise.inventory.controller;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.UrlConstants;
import com.abhi.writewise.inventory.dto.topic.TopicGenerationDTO;
import com.abhi.writewise.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.writewise.inventory.model.rest.RestApiResponse;
import com.abhi.writewise.inventory.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TopicController {

    private final TopicService topicService;

    @PostMapping(value = UrlConstants.Topic.GENERATE_TOPICS, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> generateTopics(@Valid @RequestBody TopicGenerationDTO request) {
        log.info("New request has been received to generate topics from the LLM service.");
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            TopicGenerationDTO response = topicService.generateTopicsFromLlm(request);
        });
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Submitted a request to generate the topics.");
    }

    @GetMapping(value = UrlConstants.Topic.GENERATE_TOPICS, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getAllTopics() {
        log.info("Received a request to fetch all the topics.");
        List<TopicGenerationDTO> response = topicService.findAll();
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = UrlConstants.Topic.GET_TOPIC_BY_TOPIC_ID, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> findByRefId(@PathVariable String topicRefId) {
        log.info("Received a request a fetch Topic by refId: {}", topicRefId);
        TopicGenerationDTO response = topicService.findByRefId(Long.parseLong(topicRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @DeleteMapping(value = UrlConstants.Topic.DELETE_TOPIC_BY_TOPIC_ID, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> deleteByRefId(@PathVariable String topicRefId) {
        log.info("Received a request a delete Topic by refId: {}", topicRefId);
        topicService.remove(Long.parseLong(topicRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.MOVED_PERMANENTLY).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Topic has been removed successfully.");
    }
}
