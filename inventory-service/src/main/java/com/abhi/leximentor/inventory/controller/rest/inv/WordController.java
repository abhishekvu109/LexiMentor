package com.abhi.leximentor.inventory.controller.rest.inv;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.UrlConstants;
import com.abhi.leximentor.inventory.dto.inv.WordDTO;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.service.inv.WordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordController {
    private final WordService wordService;

    @PostMapping(value = UrlConstants.Inventory.WordMetaData.WORD_ADD_WORDS, produces = ApplicationConstants.MediaType.APPLICATION_JSON, consumes = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> addWord(@Valid @RequestBody Collection<WordDTO> dto) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            Map<String, WordDTO> wordDTOS = new HashMap<>();
            for (WordDTO wordDTO : dto)
                wordDTOS.putIfAbsent(wordDTO.getWord(), wordDTO);
            Collection<WordDTO> responses = wordService.addAll(wordDTOS.values());
        });
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "You request has been submitted and is in process");
    }

    @GetMapping(value = UrlConstants.Inventory.WordMetaData.WORD_GET_BY_WORD_REF_ID, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByWordRefId(@PathVariable String wordRefId) {
        WordDTO dto = wordService.get(Long.parseLong(wordRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @GetMapping(value = UrlConstants.Inventory.WordMetaData.WORD_GET_SOURCES_BY_WORD_REF_ID, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getSourcesByWordRefId(@PathVariable String wordRefId) {
        Set<String> dto = wordService.getUniqueSourcesByWordRefId(Long.parseLong(wordRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @GetMapping(value = UrlConstants.Inventory.WordMetaData.WORD_GET_BY_WORD_REF_ID_AND_SOURCES, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getWordByWordRefIdAndSource(@PathVariable String wordRefId, @PathVariable String source) {
        WordDTO dto = wordService.getWordByWordRefIdAndSource(source, Long.parseLong(wordRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

}
