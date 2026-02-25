package com.abhi.leximentor.leximentor.controller.rest.inv;

import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.dto.inv.WordDTO;
import com.abhi.leximentor.leximentor.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
import com.abhi.leximentor.leximentor.service.inv.WordService;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping({"/api/v1/leximentor/words", "/api/leximentor/inventory/words"})
public class WordController {
    private final WordService wordService;

    @PostMapping(produces = ApplicationConstants.MediaType.APPLICATION_JSON, consumes = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> addWord(@Valid @RequestBody Collection<WordDTO> dto) {
        log.info("Add words requested. count={}", dto == null ? 0 : dto.size());
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            Map<String, WordDTO> wordDTOS = new HashMap<>();
            for (WordDTO wordDTO : dto)
                wordDTOS.putIfAbsent(wordDTO.getWord(), wordDTO);
            Collection<WordDTO> responses = wordService.addAll(wordDTOS.values());
        });
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "You request has been submitted and is in process");
    }

    @PostMapping(value = {"/metadata:generate", "/generate"}, produces = ApplicationConstants.MediaType.APPLICATION_JSON, consumes = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> generateWordMetadataFromLLM(@RequestBody Collection<String> words) {
        log.info("Generate word metadata requested. count={}", words == null ? 0 : words.size());
        Collection<WordDTO> response=new LinkedList<>();
        for(String word:words){
            response.add(wordService.generateWordMetadataFromLLM(word));
        }

        return (CollectionUtil.isNotEmpty(response))
                ? ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response)
                : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Something is wrong");
    }

    @GetMapping(value = "/{wordKey}", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByWordKey(@PathVariable String wordKey) {
        log.info("Get word by key requested. wordKey={}", wordKey);
        WordDTO dto = wordService.getByKey(wordKey);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @GetMapping(value = "/{wordKey}/sources", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getSourcesByWordKey(@PathVariable String wordKey) {
        log.info("Get sources by word key requested. wordKey={}", wordKey);
        Set<String> dto = wordService.getUniqueSourcesByWordKey(wordKey);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @GetMapping(value = "/{wordKey}/sources/{source}", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getWordByWordKeyAndSource(@PathVariable String wordKey, @PathVariable String source) {
        log.info("Get word by key and source requested. wordKey={}, source={}", wordKey, source);
        WordDTO dto = wordService.getWordByWordKeyAndSource(source, wordKey);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

}
