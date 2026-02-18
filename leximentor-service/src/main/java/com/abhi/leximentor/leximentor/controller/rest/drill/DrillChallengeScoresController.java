package com.abhi.leximentor.leximentor.controller.rest.drill;

import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
import com.abhi.leximentor.leximentor.repository.drill.DrillChallengeRepository;
import com.abhi.leximentor.leximentor.service.drill.DrillChallengeScoreService;
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

    @PutMapping(value = "/api/leximentor/drill/metadata/challenges/challenge/{challengeId}/scores", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> submitResponse(@PathVariable String challengeId, @RequestBody List<ChallengeScoresDTO> request) {
        log.info("Received a request to update the user response for questions : {}", request);
        List<ChallengeScoresDTO> response = drillChallengeScoreService.updateResponse(request);
        log.info("Successfully updated the user responses {}", response.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = "/api/leximentor/drill/metadata/challenges/challenge/{challengeId}/scores", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getChallengeScoresByChallengeId(@PathVariable String challengeId) {
        log.info("Received a request to fetch the list of drill challenge scores from the drill challenge using drill challenge id {}", challengeId);
        Challenge challenge = drillChallengeRepository.findByRefId(Long.parseLong(challengeId));
        log.info("Found the drill challenge object");
        List<ChallengeScoresDTO> ChallengeScoresDTOS = drillChallengeScoreService.getByDrillChallengeId(challenge);
        log.info("Found {} drill challenge scores objects", ChallengeScoresDTOS.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, ChallengeScoresDTOS);
    }
}
