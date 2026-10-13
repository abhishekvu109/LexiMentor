package com.abhi.leximentor.inventory.controller.rest.inv;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.dto.inv.EvaluatorDTO;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.service.inv.EvaluatorService;
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
public class EvaluatorController {
    private final EvaluatorService evaluatorService;

    @PostMapping(value = "/api/leximentor/evaluators/evaluator", produces = ApplicationConstants.MediaType.APPLICATION_JSON, consumes = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> createEvaluator(@RequestBody List<EvaluatorDTO> requests) {
        log.info("Evaluator create requested. count={}", requests == null ? 0 : requests.size());
        List<EvaluatorDTO> evaluatorDTOS = evaluatorService.addAll(requests);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, evaluatorDTOS);
    }

    @GetMapping(value = "/api/leximentor/evaluators/evaluator/name/{name}", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByName(@PathVariable String name) {
        log.info("Evaluator get by name requested. name={}", name);
        EvaluatorDTO evaluatorDTO = evaluatorService.getByName(name);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, evaluatorDTO);
    }

    @GetMapping(value = "/api/leximentor/evaluators/evaluator/type/drill/{drillType}", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByDrillType(@PathVariable String drillType) {
        log.info("Evaluator get by drill type requested. drillType={}", drillType);
        List<EvaluatorDTO> evaluatorDTOS = evaluatorService.getByDrillType(drillType);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, evaluatorDTOS);
    }


    @GetMapping(value = "/api/leximentor/evaluators/evaluator/id/{refId}", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getByRefId(@PathVariable long refId) {
        log.info("Evaluator get by refId requested. refId={}", refId);
        EvaluatorDTO evaluatorDTO = evaluatorService.getByRefId(refId);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, evaluatorDTO);
    }
}
