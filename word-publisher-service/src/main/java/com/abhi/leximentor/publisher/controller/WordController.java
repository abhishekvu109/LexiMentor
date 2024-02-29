package com.abhi.leximentor.publisher.controller;

import com.abhi.leximentor.publisher.constants.ApplicationConstants;
import com.abhi.leximentor.publisher.service.CSVReader;
import com.abhi.leximentor.publisher.service.KafkaWordPublisherService;
import com.abhi.leximentor.publisher.service.RestClientServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordController {

    private final KafkaWordPublisherService service;
    private final CSVReader csvReader;
    private final RestClientServiceImpl restClientService;

    @GetMapping(value = ApplicationConstants.KAFKA_WORD_API, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<?> post() {
        List<String> words = csvReader.getGetWords();
        for (String word : words) {
            restClientService.getResponse(word);
        }
        return ResponseEntity.ok("All the words have been posted");
    }
}
