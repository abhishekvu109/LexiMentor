package com.abhi.leximentor.inventory.controller.rest.inv;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.dto.inv.WordDTO;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.service.inv.WordService;
import com.abhi.leximentor.inventory.util.CollectionUtil;
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
public class WordController {
    private final WordService wordService;

    @PostMapping(value = "/api/leximentor/inventory/words", produces = ApplicationConstants.MediaType.APPLICATION_JSON, consumes = ApplicationConstants.MediaType.APPLICATION_JSON)
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

    @PostMapping(value = "/api/leximentor/inventory/words/generate", produces = ApplicationConstants.MediaType.APPLICATION_JSON, consumes = ApplicationConstants.MediaType.APPLICATION_JSON)
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

    @GetMapping(value = "/api/leximentor/inventory/words/{wordRefId}", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByWordRefId(@PathVariable String wordRefId) {
        log.info("Get word by refId requested. wordRefId={}", wordRefId);
        WordDTO dto = wordService.get(Long.parseLong(wordRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @GetMapping(value = "/api/leximentor/inventory/words/{wordRefId}/sources", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getSourcesByWordRefId(@PathVariable String wordRefId) {
        log.info("Get sources by word refId requested. wordRefId={}", wordRefId);
        Set<String> dto = wordService.getUniqueSourcesByWordRefId(Long.parseLong(wordRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @GetMapping(value = "/api/leximentor/inventory/words/{wordRefId}/sources/{source}", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getWordByWordRefIdAndSource(@PathVariable String wordRefId, @PathVariable String source) {
        log.info("Get word by refId and source requested. wordRefId={}, source={}", wordRefId, source);
        WordDTO dto = wordService.getWordByWordRefIdAndSource(source, Long.parseLong(wordRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

}
