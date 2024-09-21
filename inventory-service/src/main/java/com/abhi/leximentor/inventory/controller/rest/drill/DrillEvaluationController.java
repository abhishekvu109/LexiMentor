package com.abhi.leximentor.inventory.controller.rest.drill;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.constants.UrlConstants;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillEvaluationDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillReportResponseDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.exceptions.entities.ServerException;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.service.drill.DrillChallengeScoreService;
import com.abhi.leximentor.inventory.service.drill.DrillEvaluationService;
import com.abhi.leximentor.inventory.service.drill.impl.DrillServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillEvaluationController {

    private final DrillChallengeRepository drillChallengeRepository;
    private final DrillChallengeScoreService drillChallengeScoreService;
    private final DrillEvaluationService drillEvaluationService;

    @PostMapping(value = UrlConstants.Drill.DrillEvaluation.DRILL_EVALUATE_BY_CHALLENGE_ID, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> evaluateChallenge(@PathVariable String challengeId, @RequestParam String evaluator) {
        log.info("Received a request for the evaluation of the challenge {} using evaluator {}", challengeId, evaluator);
        DrillChallenge drillChallenge = drillChallengeRepository.findByRefId(Long.parseLong(challengeId));
//        if (drillChallenge.getStatus() == Status.DrillChallenge.EVALUATED)
//            throw new ServerException().new ChallengeAlreadyEvaluated("The challenge is already evaluated.");
        log.info("Successfully fetched the drill challenge objects using the challenge id {},{}", challengeId, drillChallenge);
        List<DrillChallengeScoresDTO> drillChallengeScoresDTOS = drillChallengeScoreService.getByDrillChallengeId(drillChallenge);
        log.info("Successfully fetched the {} questions to evaluates from the challenge scores entity", drillChallengeScoresDTOS.size());
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            List<DrillEvaluationDTO> drillEvaluationDTOS = drillEvaluationService.evaluate(drillChallengeScoresDTOS, evaluator, Long.parseLong(challengeId));
        });
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "The job has been successfully submitted and the evaluation is progress.");
    }

    @GetMapping(value = UrlConstants.Drill.DrillEvaluation.DRILL_GET_EVALUATION_RESULT_BY_CHALLENGE_ID, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getEvaluationReport(@PathVariable String challengeId) {
        log.info("Received a request to get the evaluation report for the challenge {}", challengeId);
        DrillReportResponseDTO reportResponseDTO = drillEvaluationService.getEvaluationReport(Long.parseLong(challengeId));
        log.info("Found the report for the challenge id {},{}", challengeId, reportResponseDTO);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, reportResponseDTO);
    }
}
