package com.abhi.writewise.inventory.controller;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.UrlConstants;
import com.abhi.writewise.inventory.dto.response.ResponseDTO;
import com.abhi.writewise.inventory.dto.response.ResponseMasterDTO;
import com.abhi.writewise.inventory.dto.response.ResponseVersionDTO;
import com.abhi.writewise.inventory.dto.topic.TopicDTO;
import com.abhi.writewise.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.writewise.inventory.model.rest.RestApiResponse;
import com.abhi.writewise.inventory.service.ResponseAndEvaluationService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class ResponseController {
    private final ResponseAndEvaluationService responseAndEvaluationService;

    @PostMapping(value = UrlConstants.ResponseAndEvaluate.CAPTURE_RESPONSE, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> collectResponse(@RequestBody CollectRequest request) {
        //responseType:0 = Draft, 1=Submitted
        ResponseMasterDTO responseMasterDTO = (request.responseType() == 0) ? responseAndEvaluationService.doSaveAsDraft(Long.parseLong(request.writingSessionRefId()), Long.parseLong(request.topicRefId()), request.response()) : responseAndEvaluationService.doSubmitResponse(Long.parseLong(request.writingSessionRefId()), Long.parseLong(request.topicRefId()), request.response());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, responseMasterDTO);
    }

    @GetMapping(value = UrlConstants.ResponseAndEvaluate.GET_RESPONSES_TOPIC_REF_ID, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getAllResponseByTopicRefId(@PathVariable String topicRefId) {
        ResponseDTO responseDTO = responseAndEvaluationService.getResponseByTopicRefId(Long.parseLong(topicRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, responseDTO);
    }

    @GetMapping(value = UrlConstants.Response.V1.GET_RESPONSE_BY_TOPIC_AND_VERSION, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getResponseByRefIdAndVersion(@PathVariable String responseRefId, @PathVariable String versionRefId) {
        ResponseDTO responseDTO = responseAndEvaluationService.getResponseByResponseRefIdAndVersionRefId(Long.parseLong(responseRefId), Long.parseLong(versionRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, responseDTO);
    }


    @GetMapping(value = UrlConstants.ResponseAndEvaluate.GET_RESPONSES_SUBMITTED, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getSubmittedResponses() {
        List<ResponseDTO> responses = responseAndEvaluationService.submittedResponses();
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, responses);
    }

    @GetMapping(value = UrlConstants.ResponseAndEvaluate.GET_RESPONSES_EVALUATED, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getEvaluatedResponses() {
        List<ResponseDTO> responses = responseAndEvaluationService.getEvaluatedTopics();
        List<EvaluatedResponse> evaluatedResponses = new ArrayList<>();
        responses.forEach(response -> {
            response.getResponseVersionDTOs().forEach(rv -> {
                evaluatedResponses.add(new EvaluatedResponse(response.getRefId(), response.getTopic(), rv));
            });
        });
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, evaluatedResponses);
    }


    private record CollectRequest(@NotBlank String writingSessionRefId, @NotBlank String topicRefId,
                                  @NotBlank int responseType, @NotBlank String response

    ) {
    }

    private record EvaluatedResponse(
            String responseRefId,
            TopicDTO topic,
            ResponseVersionDTO responseVersion

    ) {
    }
}
