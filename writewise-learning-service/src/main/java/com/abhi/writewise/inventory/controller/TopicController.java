package com.abhi.writewise.inventory.controller;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.UrlConstants;
import com.abhi.writewise.inventory.dto.LlmTopicDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TopicController {
    private final TopicService topicService;

    @PostMapping(value = UrlConstants.Topic.GENERATE_TOPICS, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> generateTopics(@Valid @RequestBody LlmTopicDTO request) {
        log.info("New request has been received to generate topics from the LLM service.");
        LlmTopicDTO response = topicService.generateTopicsFromLlm(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
