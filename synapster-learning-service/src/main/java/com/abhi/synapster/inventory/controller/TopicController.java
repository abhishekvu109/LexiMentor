package com.abhi.synapster.inventory.controller;

import com.abhi.synapster.inventory.constants.ApplicationConstants;
import com.abhi.synapster.inventory.constants.UrlConstants;
import com.abhi.synapster.inventory.dto.TopicDTO;
import com.abhi.synapster.inventory.exceptions.entities.ServerException;
import com.abhi.synapster.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.synapster.inventory.model.rest.RestApiResponse;
import com.abhi.synapster.inventory.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TopicController {
    private final TopicService topicService;

    @PostMapping(value = UrlConstants.Topic.TOPIC_ADD, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> add(List<TopicDTO> topicDTOList, String subjectRefId) {
        try {
            List<TopicDTO> list = topicService.addAll(topicDTOList, Long.parseLong(subjectRefId));
            if (CollectionUtils.isEmpty(list)) throw new ServerException().new InternalError("Internal error");
            return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, list);
        } catch (ServerException.EntityObjectNotFound ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server error");
        }
    }

    @PutMapping(value = UrlConstants.Topic.TOPIC_UPDATE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> update(TopicDTO topicDTO) {
        try {
            TopicDTO dto = topicService.update(topicDTO);
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
        } catch (ServerException.EntityObjectNotFound ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server error");
        }
    }

    @DeleteMapping(value = UrlConstants.Topic.TOPIC_DELETE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> delete(TopicDTO topicDTO) {
        try {
            topicService.delete(topicDTO);
            return ResponseEntityBuilder.getBuilder(HttpStatus.GONE).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Entity deleted");
        } catch (ServerException.EntityObjectNotFound ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server error");
        }
    }

    @GetMapping(value = UrlConstants.Topic.TOPIC_GET_BY_REF_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getByRefId(@PathVariable String topicRefId) {
        try {
            TopicDTO dto = topicService.getByRefId(Long.parseLong(topicRefId));
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
        } catch (ServerException.EntityObjectNotFound ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server error");
        }
    }

    @GetMapping(value = UrlConstants.Topic.TOPIC_GET_BY_SUBJECT_REF_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getBySubjectRefId(@PathVariable String subjectRefId) {
        try {
            List<TopicDTO> dto = topicService.getBySubjects(Long.parseLong(subjectRefId));
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
        } catch (ServerException.EntityObjectNotFound ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server error");
        }
    }

    @GetMapping(value = UrlConstants.Topic.TOPIC_GET_BY_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse> getByName(@RequestParam String name) {
        try {
            TopicDTO dto = topicService.getByName(name);
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
        } catch (ServerException.EntityObjectNotFound ex) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Internal server error");
        }
    }


}
