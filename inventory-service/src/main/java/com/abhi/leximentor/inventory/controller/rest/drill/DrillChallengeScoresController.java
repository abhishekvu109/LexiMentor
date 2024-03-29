package com.abhi.leximentor.inventory.controller.rest.drill;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.UrlConstants;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeScoresDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.inventory.service.drill.DrillChallengeScoreService;
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
public class DrillChallengeScoresController {
    private final DrillChallengeScoreService drillChallengeScoreService;
    private final DrillChallengeRepository drillChallengeRepository;

    @PutMapping(value = UrlConstants.Drill.DrillChallengeScores.DRILL_UPDATE_CHALLENGE_SCORES_BY_CHALLENGE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> submitResponse(@PathVariable String challengeId, @RequestBody List<DrillChallengeScoresDTO> request) {
        log.info("Received a request to update the user response for questions : {}", request);
        List<DrillChallengeScoresDTO> response = drillChallengeScoreService.updateResponse(request);
        log.info("Successfully updated the user responses {}", response.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = UrlConstants.Drill.DrillChallengeScores.DRILL_GET_CHALLENGE_SCORES_BY_CHALLENGE_ID, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getChallengeScoresByChallengeId(@PathVariable String challengeId) {
        log.info("Received a request to fetch the list of drill challenge scores from the drill challenge using drill challenge id {}", challengeId);
        DrillChallenge drillChallenge = drillChallengeRepository.findByRefId(Long.parseLong(challengeId));
        log.info("Found the drill challenge object");
        List<DrillChallengeScoresDTO> drillChallengeScoresDTOS = drillChallengeScoreService.getByDrillChallengeId(drillChallenge);
        log.info("Found {} drill challenge scores objects", drillChallengeScoresDTOS.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillChallengeScoresDTOS);
    }
}
