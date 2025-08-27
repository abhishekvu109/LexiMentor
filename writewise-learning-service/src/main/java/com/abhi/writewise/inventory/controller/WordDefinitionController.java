package com.abhi.writewise.inventory.controller;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.UrlConstants;
import com.abhi.writewise.inventory.dto.WordDefinitionDTO;
import com.abhi.writewise.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.writewise.inventory.model.rest.RestApiResponse;
import com.abhi.writewise.inventory.service.WordDefinitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = UrlConstants.Topic.BASE_URL)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordDefinitionController {
    private final WordDefinitionService wordDefinitionService;

    @PostMapping(value = UrlConstants.Topic.V1.GENERATE_WORD_METADATA, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> generateWordMetadata(@Valid @RequestBody WordDefinitionDTO request) {
        log.info("New request gas been received to generate the word definition from the LLM service.");
        WordDefinitionDTO response = wordDefinitionService.generateWordDefinitionFromLlm(request);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
