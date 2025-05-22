package com.abhi.leximentor.ai.controller.v2;

import com.abhi.leximentor.ai.constants.ApplicationConstants;
import com.abhi.leximentor.ai.constants.UrlConstants;
import com.abhi.leximentor.ai.rest.ResponseEntityBuilder;
import com.abhi.leximentor.ai.rest.RestApiResponse;
import com.abhi.leximentor.ai.service.LLMPromptService;
import jakarta.validation.constraints.NotBlank;
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
public class AiRestControllerV2 {

    private final LLMPromptService promptService;


    @PostMapping(value = UrlConstants.EvaluateMeaningPrompts.V2.GENERATE_STANDARD_PROMPT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> generatePromptResponse(@RequestBody PromptRequest promptRequest) {
        String response = promptService.execute(promptRequest.prompt(), promptRequest.format(), promptRequest.model());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    private record PromptRequest(
            @NotBlank String prompt,
            @NotBlank String model,
            String format
    ) {
    }
}
