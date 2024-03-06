package com.abhi.leximentor.inventory.controller.rest.drill;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.UrlConstants;
import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.service.drill.DrillMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DrillController {
    private final DrillMetadataService drillMetadataService;

    @PostMapping(value = UrlConstants.Drill.DRILL_CREATE_RANDOMLY, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> newDrillRandomly(@RequestParam int limit, @RequestParam boolean isNewWords) {
        log.info("");
        DrillMetadataDTO dto = (isNewWords) ? drillMetadataService.createDrillFromNewWords(limit) : drillMetadataService.createDrillRandomly(limit);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @PostMapping(value = UrlConstants.Drill.DRILL_CREATE_BY_SOURCE_RANDOMLY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> newDrillBySourceRandomly(@RequestParam int limit, @RequestParam boolean isNewWords, @PathVariable String sourceName) {
        DrillMetadataDTO dto = drillMetadataService.createDrillBySource(limit, sourceName, isNewWords);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }
}
