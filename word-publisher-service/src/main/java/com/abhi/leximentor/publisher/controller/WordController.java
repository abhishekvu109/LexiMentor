package com.abhi.leximentor.publisher.controller;

import com.abhi.leximentor.publisher.constants.ApplicationConstants;
import com.abhi.leximentor.publisher.service.KafkaWordPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordController {

    private final KafkaWordPublisherService service;

    @GetMapping(value = ApplicationConstants.KAFKA_WORD_TOPIC_NAME, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<?> post() {
        try {
            service.post(null);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return ResponseEntity.ok("");
    }
}
