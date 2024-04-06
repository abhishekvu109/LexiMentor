package com.abhi.synapster.inventory.controller;


import com.abhi.synapster.inventory.constants.ApplicationConstants;
import com.abhi.synapster.inventory.constants.UrlConstants;
import com.abhi.synapster.inventory.dto.ContentDTO;
import com.abhi.synapster.inventory.exceptions.entities.ServerException;
import com.abhi.synapster.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.synapster.inventory.model.rest.RestApiResponse;
import com.abhi.synapster.inventory.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContentController {
    private final ContentService contentService;

    @PostMapping(value = UrlConstants.Content.CONTENT_ADD, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<ContentDTO> contentDTOS, @RequestParam String topicRefId) {
        List<ContentDTO> contentDTOList = contentService.addAll(contentDTOS, Long.parseLong(topicRefId));
        if (CollectionUtils.isEmpty(contentDTOList))
            throw new ServerException().new InternalError("Internal problem occurred");
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, contentDTOList);
    }

    @PutMapping(value = UrlConstants.Content.CONTENT_UPDATE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> update(@RequestBody ContentDTO contentDTO) {
        ContentDTO dto = contentService.update(contentDTO);
        if (dto == null) throw new ServerException().new InternalError("Internal error occurred.");
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @DeleteMapping(value = UrlConstants.Content.CONTENT_DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> delete(ContentDTO contentDTO) {
        contentService.delete(contentDTO);
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Resource deleted successfully.");
    }

    @GetMapping(value = UrlConstants.Content.CONTENT_GET_BY_REF_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getByRefId(@PathVariable String contentRefId) {
        ContentDTO contentDTO = contentService.getByRefId(Long.parseLong(contentRefId));
        if (contentDTO == null) throw new ServerException().new InternalError("Internal error occurred.");
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, contentDTO);
    }

    @GetMapping(value = UrlConstants.Content.CONTENT_GET_BY_TOPIC, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getByTopic(@PathVariable String topicRefId) {
        List<ContentDTO> contentDTOList = contentService.getByTopic(Long.parseLong(topicRefId));
        if (CollectionUtils.isEmpty(contentDTOList))
            throw new ServerException().new InternalError("Internal error occurred.");
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, contentDTOList);
    }


}
