package com.abhi.leximentor.inventory.controller.rest.drill;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.UrlConstants;
import com.abhi.leximentor.inventory.dto.drill.DrillSetDTO;
import com.abhi.leximentor.inventory.dto.inv.WordDTO;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.service.drill.DrillSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillSetController {
    private final DrillSetService drillSetService;

    @GetMapping(value = UrlConstants.Drill.DrillSet.DRILL_GET_DRILL_SET_BY_SET_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getDrillSetBySetId(@PathVariable String drillSetRefId) {
        log.info("Received a request for getting a drill set using a drill set ref id {}", drillSetRefId);
        DrillSetDTO dto = drillSetService.getDrillSetByDrillSetId(Long.parseLong(drillSetRefId));
        log.info("Successfully obtained the drill set object from the database {}.", dto);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @GetMapping(value = UrlConstants.Drill.DrillSet.DRILL_GET_DRILL_SETS_BY_DRILL_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getDrillSetsByDrillId(@PathVariable String drillRefId) {
        log.info("Received a request to fetch the list of drill set list from the drill metadata id {}", drillRefId);
        List<DrillSetDTO> dto = drillSetService.getDrillSetsByDrillId(Long.parseLong(drillRefId));
        log.info("Successfully fetched all the drill set list from the drill-ref-id {} of size {}", drillRefId, dto.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @GetMapping(value = UrlConstants.Drill.DrillSet.DRILL_GET_WORDS_BY_DRILL_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getWordsByDrillId(@PathVariable String drillRefId) {
        log.info("Received a request to fetch the list of words in the drills from the drill metadata id {}", drillRefId);
        List<DrillSetDTO> dto = drillSetService.getDrillSetsByDrillId(Long.parseLong(drillRefId));
        List<String> words = dto.stream().map(DrillSetDTO::getWord).toList();
        log.info("Successfully fetched all the word list from the drill-ref-id {} of size {}", drillRefId, words.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, words);
    }

    @GetMapping(value = UrlConstants.Drill.DrillSet.DRILL_GET_WORDS_DATA_BY_DRILL_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getWordDataByDrillId(@PathVariable String drillRefId) {
        log.info("Received a request to fetch the list of words data in the drills from the drill metadata id {}", drillRefId);
        List<WordDTO> dto = drillSetService.getWordDataFromDrillId(Long.parseLong(drillRefId));
        log.info("Successfully fetched all the word list from the drill-ref-id {} of size {}", drillRefId, dto.size());
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }
}
