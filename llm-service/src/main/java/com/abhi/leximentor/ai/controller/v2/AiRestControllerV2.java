package com.abhi.leximentor.ai.controller.v2;

import com.abhi.leximentor.ai.constants.UrlConstants;
import com.abhi.leximentor.ai.dto.OllamaDTO;
import com.abhi.leximentor.ai.dto.OllamaOptionsDTO;
import com.abhi.leximentor.ai.dto.OllamaResponseDTO;
import com.abhi.leximentor.ai.service.LLMPromptService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AiRestControllerV2 {

    private final LLMPromptService promptService;
    private final ObjectMapper mapper;

    @PostMapping(value = UrlConstants.EvaluateMeaningPrompts.V2.GENERATE_STANDARD_PROMPT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> generatePromptResponse(@RequestBody PromptRequest promptRequest) {
        OllamaDTO request = OllamaDTO.builder().stream(false).options(OllamaOptionsDTO.builder().seed(42).build()).prompt(promptRequest.prompt()).model(promptRequest.model()).build();
        try {
            if (StringUtils.isNotEmpty(promptRequest.format())) {
                request.setFormat(mapper.readTree(promptRequest.format()));
            }
//            else {
//                request.setFormat(mapper.readTree(ApplicationConstants.LLM_EVALUATION_RESPONSE_FORMAT));
//            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Received a request: {}", request);
        OllamaResponseDTO response = promptService.execute(request);
        if (StringUtils.isEmpty(response.getResponse())) {
            throw new RuntimeException("Response is NULL.");
        }
        return ResponseEntity.ok(response.getResponse());
    }

    private record PromptRequest(String format, @NotBlank String prompt, @NotBlank String model) {
    }
}
