package com.abhi.writewise.inventory.controller;

import com.abhi.writewise.inventory.constants.ApplicationConstants;
import com.abhi.writewise.inventory.constants.UrlConstants;
import com.abhi.writewise.inventory.dto.response.ResponseMasterDTO;
import com.abhi.writewise.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.writewise.inventory.model.rest.RestApiResponse;
import com.abhi.writewise.inventory.service.ResponseAndEvaluationService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class ResponseAndEvaluationController {
    private final ResponseAndEvaluationService responseAndEvaluationService;

    @PostMapping(value = UrlConstants.ResponseAndEvaluate.CAPTURE_RESPONSE, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> collectResponse(@RequestBody CollectRequest request) {
        //responseType:0 = Draft, 1=Submitted
        ResponseMasterDTO responseMasterDTO = (request.responseType() == 0) ?
                responseAndEvaluationService.saveAsDraft(Long.parseLong(request.writingSessionRefId()), Long.parseLong(request.topicRefId()), request.response()) :
                responseAndEvaluationService.submit(Long.parseLong(request.writingSessionRefId()), Long.parseLong(request.topicRefId()), request.response());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, responseMasterDTO);
    }

    @PostMapping(value = UrlConstants.ResponseAndEvaluate.RESPONSE_EVALUATE, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> evaluate(@RequestBody EvaluateRequest evaluateRequest) {
        ResponseMasterDTO responseMasterDTO = responseAndEvaluationService.evaluate(Long.parseLong(evaluateRequest.writingSessionRefId()), Long.parseLong(evaluateRequest.topicRefId()));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, responseMasterDTO);
    }

    public record EvaluateRequest(@NotBlank String writingSessionRefId, @NotBlank String topicRefId) {
    }

    private record CollectRequest(@NotBlank String writingSessionRefId, @NotBlank String topicRefId,
                                  @NotBlank int responseType,
                                  @NotBlank String response

    ) {
    }
}
