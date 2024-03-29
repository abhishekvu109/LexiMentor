package com.abhi.leximentor.inventory.controller.rest.drill;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.DrillTypes;
import com.abhi.leximentor.inventory.constants.UrlConstants;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeDTO;
import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.service.drill.DrillChallengeService;
import com.abhi.leximentor.inventory.service.drill.DrillMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillChallengeController {

    private final DrillMetadataService drillMetadataService;
    private final DrillChallengeService drillChallengeService;

    @PostMapping(value = UrlConstants.Drill.DrillChallenge.DRILL_CHALLENGE_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> addChallenges(@RequestParam String drillId, @RequestParam String drillType) {
        log.info("Received a request to add a {} challenge to a drill id {}.", drillType, drillId);
        DrillMetadataDTO drillMetadataDTO = drillMetadataService.getByRefId(Long.parseLong(drillId));
        drillMetadataDTO = drillChallengeService.addChallenges(drillMetadataDTO, DrillTypes.getType(drillType));
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillMetadataDTO);
    }

    @GetMapping(value = UrlConstants.Drill.DrillChallenge.DRILL_GET_CHALLENGES_BY_DRILL_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getAllChallengesByDrillRefId(@PathVariable String drillRefId) {
        log.info("Received a request to fetch all the list of challenges associated to the drill. {}", drillRefId);
        List<DrillChallengeDTO> drillChallengeDTOList = drillChallengeService.getChallengesByDrillRefId(Long.parseLong(drillRefId));
        return drillChallengeDTOList != null ? ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillChallengeDTOList) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve challenges");
    }

    @DeleteMapping(value = UrlConstants.Drill.DrillChallenge.DRILL_DELETE_CHALLENGES_BY_DRILL_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> deleteChallengeByDrillRefId(@PathVariable String drillRefId) {
        log.info("Received a request to delete a challenge. Challenge ID: {}", drillRefId);
        drillChallengeService.deleteChallenge(Long.parseLong(drillRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "The Challenge has been removed successfully.");
    }
}
