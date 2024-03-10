package com.abhi.leximentor.inventory.controller.rest.drill;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.constants.UrlConstants;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillSetDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillSet;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.service.drill.DrillChallengeScoreService;
import com.abhi.leximentor.inventory.service.drill.DrillChallengeService;
import com.abhi.leximentor.inventory.service.drill.DrillMetadataService;
import com.abhi.leximentor.inventory.service.drill.DrillSetService;
import com.abhi.leximentor.inventory.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillController {
    private final DrillMetadataService drillMetadataService;
    private final DrillChallengeService drillChallengeService;
    private final DrillChallengeRepository drillChallengeRepository;
    private final DrillChallengeScoreService drillChallengeScoreService;
    private final DrillSetService drillSetService;

    @PostMapping(value = UrlConstants.Drill.DRILL_CREATE_RANDOMLY, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> newDrillRandomly(@RequestParam int limit, @RequestParam boolean isNewWords) {
        log.info("");
        DrillMetadataDTO dto = (isNewWords) ? drillMetadataService.createDrillFromNewWords(limit) : drillMetadataService.createDrillRandomly(limit);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @DeleteMapping(value = UrlConstants.Drill.DRILL_CREATE_RANDOMLY, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> deleteByRefId(@RequestParam long refId) {
        drillMetadataService.deleteByRefId(refId);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "The data has been removed successfully.");
    }

    @PostMapping(value = UrlConstants.Drill.DRILL_CREATE_BY_SOURCE_RANDOMLY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> newDrillBySourceRandomly(@RequestParam int limit, @RequestParam boolean isNewWords, @PathVariable String sourceName) {
        DrillMetadataDTO dto = drillMetadataService.createDrillBySource(limit, sourceName, isNewWords);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @PostMapping(value = UrlConstants.Drill.DRILL_CREATE_CHALLENGES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> addChallenges(@RequestParam long drillId, @RequestParam String drillType) {
        DrillMetadataDTO drillMetadataDTO = drillMetadataService.getByRefId(drillId);
        drillMetadataDTO = drillChallengeService.addChallenges(drillMetadataDTO, DrillTypes.getType(drillType));
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillMetadataDTO);
    }

    @GetMapping(value = UrlConstants.Drill.DRILL_CREATE_RANDOMLY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getAllDrills() {
        List<DrillMetadataDTO> drillMetadataDTOList = drillMetadataService.getDrills();
        return CollectionUtil.isNotEmpty(drillMetadataDTOList) ? ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillMetadataDTOList) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve drills");
    }

    @GetMapping(value = UrlConstants.Drill.DRILL_GET_CHALLENGES_BY_DRILL_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getAllChallengesByDrillRefId(@PathVariable long drillId) {
        List<DrillChallengeDTO> drillChallengeDTOList = drillChallengeService.getChallengesByDrillRefId(drillId);
        return CollectionUtil.isNotEmpty(drillChallengeDTOList) ? ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillChallengeDTOList) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve challenges");
    }

    @GetMapping(value = UrlConstants.Drill.DRILL_CHALLENGE_SCORE_BY_CHALLENGE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getAllChallengeScoresByChallengeId(@PathVariable long challengeId) {
        DrillChallenge drillChallenge = drillChallengeRepository.findByRefId(challengeId);
        List<DrillChallengeScoresDTO> drillChallengeScoresDTOS = drillChallengeScoreService.getByDrillChallengeId(drillChallenge);
        return CollectionUtil.isNotEmpty(drillChallengeScoresDTOS) ? ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillChallengeScoresDTOS) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve challenge scores");
    }

    @GetMapping(value = UrlConstants.Drill.DRILL_CHALLENGE_SET_BY_SET_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getDrillSetBySetId(@PathVariable long setId) {
        DrillSetDTO dto = drillSetService.getDrillSetByDrillId(setId);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

}
