package com.abhi.leximentor.leximentor.controller.rest.drill;

import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.dto.drill.DrillSetDTO;
import com.abhi.leximentor.leximentor.dto.inv.WordDTO;
import com.abhi.leximentor.leximentor.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
import com.abhi.leximentor.leximentor.service.drill.DrillSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping({"/api/v1/leximentor/drill-sets", "/api/leximentor/drill/set"})
public class DrillSetController {
    private final DrillSetService drillSetService;

    @GetMapping(value = "/{drillSetKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getDrillSetBySetId(@PathVariable String drillSetKey) {
        log.info("Received a request for getting a drill set using key {}", drillSetKey);
        DrillSetDTO dto = drillSetService.getDrillSetByKey(drillSetKey);
        log.info("Successfully obtained the drill set object from the database {}.", dto);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @GetMapping(value = {"/by-drill/{drillKey}/words", "/get-words/{drillKey}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getWordsByDrillId(@PathVariable String drillKey) {
        log.info("Received a request to fetch the list of words in the drill using key {}", drillKey);
        List<DrillSetDTO> dto = drillSetService.getDrillSetsByDrillId(drillKey);
        List<String> words = dto.stream().map(DrillSetDTO::getWord).toList();
        log.info("Successfully fetched all word list from drill key {} of size {}", drillKey, words.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, words);
    }

    @GetMapping(value = {"/by-drill/{drillKey}/word-data", "/api/leximentor/drill/metadata/sets/words/data/{drillKey}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getWordDataByDrillId(@PathVariable String drillKey) {
        log.info("Received a request to fetch word data in drill {}", drillKey);
        List<WordDTO> dto = drillSetService.getWordDataFromDrillId(drillKey);
        log.info("Successfully fetched all word data from drill key {} of size {}", drillKey, dto.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @GetMapping(value = {"/by-drill/{drillKey}", "/api/leximentor/drill/metadata/sets/{drillKey}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getDrillSetsByDrillId(@PathVariable String drillKey) {
        log.info("Received a request to fetch drill sets for drill key {}", drillKey);
        List<DrillSetDTO> dto = drillSetService.getDrillSetsByDrillId(drillKey);
        log.info("Successfully fetched drill sets for key {} of size {}", drillKey, dto.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }
}
