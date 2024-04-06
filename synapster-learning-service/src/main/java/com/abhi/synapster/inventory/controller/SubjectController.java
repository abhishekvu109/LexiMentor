package com.abhi.synapster.inventory.controller;

import com.abhi.synapster.inventory.constants.ApplicationConstants;
import com.abhi.synapster.inventory.constants.UrlConstants;
import com.abhi.synapster.inventory.dto.SubjectDTO;
import com.abhi.synapster.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.synapster.inventory.model.rest.RestApiResponse;
import com.abhi.synapster.inventory.service.SubjectService;
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
public class SubjectController {
    private final SubjectService subjectService;

    @PostMapping(value = UrlConstants.Subject.SUBJECT_ADD, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<SubjectDTO> subjectDTOS) {
        List<SubjectDTO> list = subjectService.addAll(subjectDTOS);
        if (CollectionUtils.isEmpty(list))
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Something wrong has happened. Please try again.");
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, list);
    }

    @PutMapping(value = UrlConstants.Subject.SUBJECT_UPDATE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody SubjectDTO subjectDTO) {
        SubjectDTO dto = subjectService.update(subjectDTO);
        if (dto == null)
            return ResponseEntityBuilder.getBuilder(HttpStatus.INTERNAL_SERVER_ERROR).errorResponse(ApplicationConstants.REQUEST_FAILURE_DESCRIPTION, "Something wrong has happened. Please try again.");
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }

    @DeleteMapping(value = UrlConstants.Subject.SUBJECT_DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody SubjectDTO subjectDTO) {
        subjectService.delete(subjectDTO);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Successfully deleted");
    }

    @GetMapping(value = UrlConstants.Subject.SUBJECT_GET_BY_REF_ID, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> getByRefId(@PathVariable String subjectRefId) {
        SubjectDTO dto = subjectService.getByRefId(Long.parseLong(subjectRefId));
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, dto);
    }
}
