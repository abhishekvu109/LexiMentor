package com.abhi.leximentor.inventory.controller.rest.drill;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.constants.UrlConstants;
import com.abhi.leximentor.inventory.dto.drill.DrillMetadataDTO;
import com.abhi.leximentor.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.inventory.model.rest.RestApiResponse;
import com.abhi.leximentor.inventory.service.drill.DrillMetadataService;
import com.abhi.leximentor.inventory.util.CollectionUtil;
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
public class DrillMetadataController {
    private final DrillMetadataService drillMetadataService;

    /*
    Added the new drill for the Random words.
     */
    @PostMapping(value = UrlConstants.Drill.DrillMetadata.DRILL_METADATA_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> drillAdd(@RequestParam int limit, @RequestParam boolean isNewWords) {
        log.info("Received a request for creating new drill from new words: Limit:{}, New words:{}", limit, isNewWords);
        DrillMetadataDTO dto = (isNewWords) ? drillMetadataService.createDrillFromNewWords(limit) : drillMetadataService.createDrillRandomly(limit);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @PostMapping(value = UrlConstants.Drill.DrillMetadata.DRILL_METADATA_ADD_BY_SOURCE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> newDrillBySourceRandomly(@RequestParam int limit, @RequestParam boolean isNewWords, @PathVariable String sourceName) {
        DrillMetadataDTO dto = drillMetadataService.createDrillBySource(limit, sourceName, isNewWords);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @DeleteMapping(value = UrlConstants.Drill.DrillMetadata.DRILL_METADATA_DELETE_BY_REF_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> deleteDrillMetadataByRefId(@PathVariable String drillRefId) {
        drillMetadataService.deleteByRefId(Long.parseLong(drillRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "The data has been removed successfully.");
    }

    @GetMapping(value = UrlConstants.Drill.DrillMetadata.DRILL_METADATA_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getAllDrills() {
        List<DrillMetadataDTO> drillMetadataDTOList = drillMetadataService.getDrills();
        return CollectionUtil.isNotEmpty(drillMetadataDTOList) ? ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, drillMetadataDTOList) : ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Unable to retrieve drills");
    }

}
