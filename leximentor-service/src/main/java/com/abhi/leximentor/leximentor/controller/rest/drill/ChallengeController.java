package com.abhi.leximentor.leximentor.controller.rest.drill;

import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.dto.drill.ChallengeDTO;
import com.abhi.leximentor.leximentor.dto.drill.DrillDTO;
import com.abhi.leximentor.leximentor.dto.drill.filters.ChallengeSearchFilter;
import com.abhi.leximentor.leximentor.dto.inv.EvaluatorDTO;
import com.abhi.leximentor.leximentor.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
import com.abhi.leximentor.leximentor.service.drill.ChallengeService;
import com.abhi.leximentor.leximentor.service.drill.DrillService;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping({"/api/leximentor/v1/challenges", "/api/leximentor/drill/metadata/challenges"})
public class ChallengeController {

    private final DrillService drillService;
    private final ChallengeService challengeService;

    @PostMapping(value = {"", "/challenge"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> addChallenges(@RequestParam String drillId, @RequestParam String drillType, @RequestParam String username) {
        log.info("Received a request to add a {} challenge to a drill id {}.", drillType, drillId);
        DrillDTO drillDTO = drillService.getByRefId(Long.parseLong(drillId));
        drillDTO = challengeService.addChallenges(drillDTO, ChallengeType.of(drillType), username);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillDTO);
    }

    @GetMapping(value = "/{challengeRefId}/evaluators", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getEvaluatorsByChallengeRefIdPath(@PathVariable String challengeRefId) {
        log.info("Received a request to fetch all the evaluators for the challenge. {}", challengeRefId);
        List<EvaluatorDTO> evaluatorDTOS = challengeService.getEvaluatorsByChallengeId(Long.parseLong(challengeRefId));
        return CollectionUtil.isEmpty(evaluatorDTOS) ? ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve evaluators") : ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, evaluatorDTOS);
    }

    @DeleteMapping(value = "/{challengeRefId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> deleteChallengeByDrillRefId(@PathVariable String challengeRefId) {
        log.info("Received a request to delete a challenge. Challenge ID: {}", challengeRefId);
        challengeService.deleteChallenge(Long.parseLong(challengeRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "The Challenge has been removed successfully.");
    }

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody  ResponseEntity<RestApiResponse> searchChallenges(@RequestParam String username, @RequestBody ChallengeSearchFilter filter) {
        log.info("Received a request to search challenges for username={}", username);
        if (StringUtils.isBlank(username)) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST).errorResponse(
                    ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "username is mandatory.");
        }
        filter.setUsername(username);
        Optional<List<ChallengeDTO>> searchResult = challengeService.search(filter);
        return searchResult
                .map(challengeDTOS -> ResponseEntityBuilder.getBuilder(HttpStatus.OK)
                        .successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, challengeDTOS))
                .orElseGet(() -> ResponseEntityBuilder.getBuilder(HttpStatus.BAD_REQUEST)
                        .errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal Server Error."));
    }

}
