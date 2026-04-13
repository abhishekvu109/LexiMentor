package com.abhi.leximentor.leximentor.controller.rest.inv;

import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.dto.inv.EvaluatorDTO;
import com.abhi.leximentor.leximentor.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
import com.abhi.leximentor.leximentor.service.inv.EvaluatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping({"/api/v1/leximentor/evaluators", "/api/leximentor/evaluators/evaluator"})
public class EvaluatorController {
    private final EvaluatorService evaluatorService;

    @PostMapping(produces = ApplicationConstants.MediaType.APPLICATION_JSON, consumes = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> createEvaluator(@RequestBody List<EvaluatorDTO> requests) {
        log.info("Evaluator create requested. count={}", requests == null ? 0 : requests.size());
        List<EvaluatorDTO> evaluatorDTOS = evaluatorService.addAll(requests);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, evaluatorDTOS);
    }

    @GetMapping(value = "/name/{name}", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByName(@PathVariable String name) {
        log.info("Evaluator get by name requested. name={}", name);
        EvaluatorDTO evaluatorDTO = evaluatorService.getByName(name);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, evaluatorDTO);
    }

    @GetMapping(value = {"/drill-types/{drillType}", "/type/drill/{drillType}"}, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByDrillType(@PathVariable String drillType) {
        log.info("Evaluator get by drill type requested. drillType={}", drillType);
        List<EvaluatorDTO> evaluatorDTOS = evaluatorService.getByDrillType(ChallengeType.of(drillType));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, evaluatorDTOS);
    }


    @GetMapping(value = {"/{key}", "/id/{key}"}, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByKey(@PathVariable String key) {
        log.info("Evaluator get by key requested. key={}", key);
        EvaluatorDTO evaluatorDTO = evaluatorService.getByKey(key);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, evaluatorDTO);
    }
}
