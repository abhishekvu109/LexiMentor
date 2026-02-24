package com.abhi.leximentor.leximentor.controller.rest.drill;

import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeScoresDTO;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
import com.abhi.leximentor.leximentor.repository.drill.ChallengeRepository;
import com.abhi.leximentor.leximentor.service.drill.ChallengeScoreService;
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
@RequestMapping({"/api/v1/leximentor/challenges", "/api/leximentor/drill/metadata/challenges/challenge"})
public class ChallengeScoresController {
    private final ChallengeScoreService challengeScoreService;
    private final ChallengeRepository challengeRepository;

    @PutMapping(value = "/{challengeId}/scores", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> submitResponse(@PathVariable String challengeId, @RequestBody List<ChallengeScoresDTO> request) {
        log.info("Received a request to update the user response for questions : {}", request);
        List<ChallengeScoresDTO> response = challengeScoreService.updateResponse(request);
        log.info("Successfully updated the user responses {}", response.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = "/{challengeId}/scores", produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> getChallengeScoresByChallengeId(@PathVariable String challengeId) {
        log.info("Received a request to fetch the list of drill challenge scores from the drill challenge using drill challenge id {}", challengeId);
        Challenge challenge = challengeRepository.findByRefId(Long.parseLong(challengeId));
        log.info("Found the drill challenge object");
        List<ChallengeScoresDTO> ChallengeScoresDTOS = challengeScoreService.getByDrillChallengeId(challenge);
        log.info("Found {} drill challenge scores objects", ChallengeScoresDTOS.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, ChallengeScoresDTOS);
    }
}
