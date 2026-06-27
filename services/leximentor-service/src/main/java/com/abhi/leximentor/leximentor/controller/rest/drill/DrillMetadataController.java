package com.abhi.leximentor.leximentor.controller.rest.drill;

import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.dto.drill.DrillDTO;
import com.abhi.leximentor.leximentor.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
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

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping({"/api/v1/leximentor/drills", "/api/leximentor/drill"})
public class DrillMetadataController {
    private final DrillService drillService;

    /*
    Added the new drill for the Random words.
     */
    @PostMapping
    public @ResponseBody ResponseEntity<RestApiResponse> drillAdd(@RequestParam int limit, @RequestParam boolean isNewWords, @RequestParam(required = false) String sourceName) {
        if (StringUtils.isNotEmpty(sourceName)) {
            log.info("Received a request for creating new drill by source. sourceName={}, limit={}, isNewWords={}", sourceName, limit, isNewWords);
            DrillDTO dto = drillService.createDrillBySource(limit, sourceName, isNewWords);
            return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
        } else {
            log.info("Received a request for creating new drill from new words: Limit:{}, New words:{}", limit, isNewWords);
            DrillDTO dto = (isNewWords) ? drillService.createDrillFromNewWords(limit) : drillService.createDrillFromExistingWords(limit);
            return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
        }
    }

    @DeleteMapping(value = "/{drillKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> deleteDrillMetadataByKey(@PathVariable String drillKey) {
        log.info("Delete drill metadata requested. drillKey={}", drillKey);
        drillService.deleteByKey(drillKey);
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "The data has been removed successfully.");
    }

    @GetMapping
    public ResponseEntity<RestApiResponse> getAllDrills() {
        log.info("Get all drills requested");
        List<DrillDTO> drillDTOList = drillService.getDrills();
        return drillDTOList != null ? ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillDTOList) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve drills");
    }

    @GetMapping(value = {"/{drillKey}/words", "/get-words/{drillKey}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getWordsByDrillKey(@PathVariable String drillKey) {
        log.info("Received a request to get words in drill by key: {}", drillKey);
        Collection<String> words = drillService.getWordsInStrByDrillKey(drillKey);
        return CollectionUtil.isNotEmpty(words) ? ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, words) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve words in the drills");
    }

    @PostMapping(value = {"/{drillKey}/name-assignment", "/assign-name/{drillKey}"}, produces = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> assignNameToDrill(@PathVariable String drillKey) {
        log.info("Assign name requested. drillKey={}", drillKey);
        DrillDTO response = drillService.assignDrillName(drillKey);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }



}
