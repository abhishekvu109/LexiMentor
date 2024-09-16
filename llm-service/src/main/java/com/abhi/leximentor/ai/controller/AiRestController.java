package com.abhi.leximentor.ai.controller;

import com.abhi.leximentor.ai.constants.ApplicationConstants;
import com.abhi.leximentor.ai.constants.UrlConstants;
import com.abhi.leximentor.ai.dto.MeaningEvaluationDTO;
import com.abhi.leximentor.ai.service.OllamaLlmChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AiRestController {
    private final OllamaLlmChatService chatService;

//    @PostMapping(value = UrlConstants.EvaluateMeaningPrompts.GENERATE_PROMPT_URL, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = MediaType.TEXT_PLAIN_VALUE)
//    public String generate(@RequestBody MeaningEvaluationDTO prompt) {
//        log.info("Request body {}", prompt);
//        return chatService.getPromptResult(prompt.getText());
//    }

    //    @PostMapping(value = UrlConstants.EvaluateMeaningPrompts.GENERATE_PROMPT_URL, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
//    public @ResponseBody ResponseEntity<RestApiResponse> evaluateMeaning(@RequestBody MeaningEvaluationDTO dto, @PathVariable String modelName) {
//        log.info("Received a request to evaluate the meaning using model- {}, prompt- {}", modelName, dto);
//        MeaningEvaluationDTO meaningEvaluationDTO = chatService.evaluate(dto.getPrompt());
//        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, meaningEvaluationDTO);
//    }
    @PostMapping(value = UrlConstants.EvaluateMeaningPrompts.GENERATE_PROMPT_URL, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<MeaningEvaluationDTO> evaluateMeaning(@RequestBody MeaningEvaluationDTO dto, @PathVariable String modelName) {
        log.info("Received a request to evaluate the meaning using model- {}, prompt- {}", modelName, dto);
        MeaningEvaluationDTO meaningEvaluationDTO = chatService.evaluate(dto.getPrompt());
        log.info("The JSON response as below: {}", meaningEvaluationDTO);
        return ResponseEntity.ok(meaningEvaluationDTO);
    }
}
