package com.abhi.leximentor.inventory.controller.rest.drill;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.constants.UrlConstants;
import com.abhi.leximentor.inventory.dto.drill.*;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.service.drill.*;
import com.abhi.leximentor.inventory.service.drill.impl.DrillServiceUtil;
import com.abhi.leximentor.inventory.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillController {
    private final DrillMetadataService drillMetadataService;
    private final DrillChallengeService drillChallengeService;
    private final DrillChallengeRepository drillChallengeRepository;
    private final DrillChallengeScoreService drillChallengeScoreService;
    private final DrillSetService drillSetService;
    private final DrillEvaluationService drillEvaluationService;

    @PostMapping(value = UrlConstants.Drill.DRILL_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> newDrillRandomly(@RequestParam int limit, @RequestParam boolean isNewWords) {
        log.info("");
        DrillMetadataDTO dto = (isNewWords) ? drillMetadataService.createDrillFromNewWords(limit) : drillMetadataService.createDrillRandomly(limit);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @DeleteMapping(value = UrlConstants.Drill.DRILL_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> deleteDrillMetadataByRefId(@RequestParam String refId) {
        drillMetadataService.deleteByRefId(Long.parseLong(refId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "The data has been removed successfully.");
    }

    @PostMapping(value = UrlConstants.Drill.DRILL_ADD_BY_SOURCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> newDrillBySourceRandomly(@RequestParam int limit, @RequestParam boolean isNewWords, @PathVariable String sourceName) {
        DrillMetadataDTO dto = drillMetadataService.createDrillBySource(limit, sourceName, isNewWords);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @PostMapping(value = UrlConstants.Drill.DRILL_ASSIGN_CHALLENGES_TO_DRILLS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> addChallenges(@RequestParam String drillId, @RequestParam String drillType) {
        DrillMetadataDTO drillMetadataDTO = drillMetadataService.getByRefId(Long.valueOf(drillId));
        drillMetadataDTO = drillChallengeService.addChallenges(drillMetadataDTO, DrillTypes.getType(drillType));
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillMetadataDTO);
    }

    @GetMapping(value = UrlConstants.Drill.DRILL_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getAllDrills() {
        List<DrillMetadataDTO> drillMetadataDTOList = drillMetadataService.getDrills();
        return CollectionUtil.isNotEmpty(drillMetadataDTOList) ? ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillMetadataDTOList) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve drills");
    }

    @GetMapping(value = UrlConstants.Drill.DRILL_GET_CHALLENGES_BY_DRILL_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getAllChallengesByDrillRefId(@PathVariable String drillId) {
        List<DrillChallengeDTO> drillChallengeDTOList = drillChallengeService.getChallengesByDrillRefId(Long.parseLong(drillId));
        return CollectionUtil.isNotEmpty(drillChallengeDTOList) ? ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillChallengeDTOList) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve challenges");
    }

    @GetMapping(value = UrlConstants.Drill.DRILL_GET_CHALLENGE_SCORE_BY_CHALLENGE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getAllChallengeScoresByChallengeId(@PathVariable String challengeId) {
        DrillChallenge drillChallenge = drillChallengeRepository.findByRefId(Long.parseLong(challengeId));
        List<DrillChallengeScoresDTO> drillChallengeScoresDTOS = drillChallengeScoreService.getByDrillChallengeId(drillChallenge);
        return CollectionUtil.isNotEmpty(drillChallengeScoresDTOS) ? ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillChallengeScoresDTOS) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve challenge scores");
    }

    @GetMapping(value = UrlConstants.Drill.DRILL_GET_DRILL_SET_BY_SET_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getDrillSetBySetId(@PathVariable String setId) {
        DrillSetDTO dto = drillSetService.getDrillSetByDrillSetId(Long.parseLong(setId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @GetMapping(value = UrlConstants.Drill.DRILL_GET_DRILL_SETS_BY_DRILL_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getDrillSetsByDrillId(@PathVariable String drillRefId) {
        List<DrillSetDTO> dto = drillSetService.getDrillSetsByDrillId(Long.parseLong(drillRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

//    @PostMapping(value = UrlConstants.Drill.DRILL_EVALUATE_BY_DRILL_ID, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
//    public @ResponseBody ResponseEntity<RestApiResponse> evaluateMeaning(@RequestBody List<DrillChallengeScoresDTO> drillChallengeScoresDTOS, @RequestParam String evaluator) {
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            List<DrillEvaluationDTO> drillEvaluationDTOS = drillEvaluationService.evaluateMeaning(drillChallengeScoresDTOS, evaluator);
//        });
//        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "The job has been successfully submitted and the evaluation is progress.");
//    }

    @PostMapping(value = UrlConstants.Drill.DRILL_EVALUATE_BY_DRILL_CHALLENGE_ID, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> evaluateChallenge(@RequestParam String challengeId, @RequestParam String evaluator) {
        DrillChallenge drillChallenge = drillChallengeRepository.findByRefId(Long.parseLong(challengeId));
        List<DrillChallengeScoresDTO> drillChallengeScoresDTOS = drillChallengeScoreService.getByDrillChallengeId(drillChallenge);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            List<DrillEvaluationDTO> drillEvaluationDTOS = drillEvaluationService.evaluateMeaning(drillChallengeScoresDTOS, evaluator);
        });
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "The job has been successfully submitted and the evaluation is progress.");
    }

}
