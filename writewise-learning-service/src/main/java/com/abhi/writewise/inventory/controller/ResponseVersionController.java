package com.abhi.writewise.inventory.controller;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.UrlConstants;
import com.abhi.writewise.inventory.dto.response.ResponseVersionDTO;
import com.abhi.writewise.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.writewise.inventory.model.rest.RestApiResponse;
import com.abhi.writewise.inventory.service.ResponseVersionService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = UrlConstants.ResponseVersion.BASE_URL)
public class ResponseVersionController {
    private final ResponseVersionService responseVersionService;

    @GetMapping(value = UrlConstants.ResponseVersion.V1.GET_RESPONSE_VERSION_BY_TOPIC_REF_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> getResponseVersionByTopicAndVersion(@PathVariable String topicRefId, @PathVariable String versionRefId) {
        log.info("Received a request to fetch Response Version object by TopicRefId:{} and versionRefId:{}.", topicRefId, versionRefId);
        ResponseVersionDTO response = responseVersionService.getResponseVersion(Long.parseLong(topicRefId), Long.parseLong(versionRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }


    @GetMapping(value = UrlConstants.ResponseVersion.V1.GENERATE_PROMPT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> getPromptByTopicAndVersion(@PathVariable String topicRefId, @PathVariable String versionRefId, @RequestBody LLMEvaluationResponseDTO request) {
        log.info("Received a request to generate the prompt by TopicRefId:{} and versionRefId:{}.", topicRefId, versionRefId);
        String response = responseVersionService.getPrompt(Long.parseLong(topicRefId), Long.parseLong(versionRefId), request.isHighLevel());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @PostMapping(value = UrlConstants.ResponseVersion.V1.EVALUATE_RESPONSE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> evaluatePrompt(@RequestBody LLMEvaluationResponseDTO request) {
        if (StringUtils.isAnyEmpty(request.topicRefId(), request.versionRefId())) {
            log.error("The API requires topicRefId and versionRefId");
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "The API requires topicRefId and versionRefId.");
        }
        log.info("Received a request to evaluate TopicRefId:{} and versionRefId:{}.", request.topicRefId, request.versionRefId);
        setModelInResponseVersionService(request.model());
        setPromptInResponseVersionService(request.prompt());
        CompletableFuture.runAsync(() -> {
            String response = responseVersionService.doEvaluationForTopicAndVersion(Long.parseLong(request.topicRefId), Long.parseLong(request.versionRefId), request.isHighLevel());
        });

        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Your request has been submitted for high-level evaluation.");
    }

    @PostMapping(value = UrlConstants.ResponseVersion.V1.SUBMIT_EVALUATION_RESULTS, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> submitEvaluationResults(@RequestBody LLMEvaluationResponseDTO request) {
        if (StringUtils.isAnyEmpty(request.topicRefId(), request.versionRefId())) {
            log.error("The API requires topicRefId and versionRefId");
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "The API requires topicRefId and versionRefId.");
        }
        log.info("Received a request to submit evaluation results TopicRefId:{} and versionRefId:{}.", request.topicRefId, request.versionRefId);
        setModelInResponseVersionService(StringUtils.isNotEmpty(request.model()) ? request.model() : "gemma3");
        ResponseVersionDTO response = responseVersionService.doSubmitResult(Long.parseLong(request.topicRefId), Long.parseLong(request.versionRefId), request.isHighLevel);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @PostMapping(value = UrlConstants.ResponseVersion.V1.VALIDATE_EVALUATION_RESPONSE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> validateEvaluationResults(@RequestBody LLMEvaluationResponseDTO request) {
        if (StringUtils.isAnyEmpty(request.topicRefId(), request.versionRefId())) {
            log.error("The API requires topicRefId and versionRefId");
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "The API requires topicRefId and versionRefId.");
        }
        log.info("Received a request to validate evaluation results TopicRefId:{} and versionRefId:{}.", request.topicRefId, request.versionRefId);
        boolean response = responseVersionService.doValidateEvaluationResult(Long.parseLong(request.topicRefId), Long.parseLong(request.versionRefId), request.isHighLevel);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @DeleteMapping(value = UrlConstants.ResponseVersion.V1.DELETE_ALL_EVALUATIONS_FOR_TOPIC, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> performDeleteAllEvaluationsForTopic(@PathVariable String topicRefId) {
        log.info("Received a request to delete all evaluations for all a topic: {}", topicRefId);
        responseVersionService.doDeleteEvaluationAll(Long.parseLong(topicRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.MOVED_PERMANENTLY).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "We have removed all the evaluations for a topic.");
    }

    @DeleteMapping(value = UrlConstants.ResponseVersion.V1.DELETE_SELECTED_EVALUATIONS_FOR_TOPIC, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> performDeleteAllEvaluationsForTopic(@PathVariable String topicRefId, @PathVariable String versionRefId) {
        log.info("Received a request to delete a evaluation {} for all a topic: {}", versionRefId, topicRefId);
        responseVersionService.doDeleteEvaluationByVersion(Long.parseLong(topicRefId), Long.parseLong(versionRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.MOVED_PERMANENTLY).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "We have removed an evaluation for a topic.");
    }

    private void setModelInResponseVersionService(String model) {
        responseVersionService.setModelName(StringUtils.isNotEmpty(model) ? model : "");
    }

    private void setPromptInResponseVersionService(String prompt) {
        responseVersionService.setPrompt(StringUtils.isNotEmpty(prompt) ? prompt : "");
    }

    private record LLMEvaluationResponseDTO(String prompt, String model, @NotBlank boolean isHighLevel,
                                            String topicRefId, String versionRefId) {
    }
}
