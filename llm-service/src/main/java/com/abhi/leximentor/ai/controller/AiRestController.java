package com.abhi.leximentor.ai.controller;

import com.abhi.leximentor.ai.constants.ApplicationConstants;
import com.abhi.leximentor.ai.constants.UrlConstants;
import com.abhi.leximentor.ai.dto.MeaningEvaluationDTO;
import com.abhi.leximentor.ai.dto.TextPromptDTO;
import com.abhi.leximentor.ai.service.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AiRestController {
    private final LlmService chatService;

    @PostMapping(value = UrlConstants.EvaluateMeaningPrompts.GENERATE_PROMPT_URL, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<MeaningEvaluationDTO> evaluateMeaning(@RequestBody MeaningEvaluationDTO dto, @PathVariable String modelName) {
        log.info("Received a request to evaluate the meaning using model- {}, prompt- {}", modelName, dto);
        MeaningEvaluationDTO meaningEvaluationDTO = chatService.evaluateWordMeaning(dto.getPrompt());
        log.info("The JSON response as below: {}", meaningEvaluationDTO);
        return ResponseEntity.ok(meaningEvaluationDTO);
    }

    @PostMapping(value = UrlConstants.EvaluateMeaningPrompts.GENERATE_STANDARD_PROMPT, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<String> textPrompt(@RequestBody TextPromptDTO dto, @PathVariable String modelName) {
        log.info("Received a request for standard text prompt using model- {}, prompt- {}", modelName, dto);
        String promptResponse = chatService.prompt(dto.getPrompt());
        log.info("The String response as below: {}", promptResponse);
        return ResponseEntity.ok(promptResponse);
    }
}
