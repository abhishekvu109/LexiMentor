package com.abhi.synapster.inventory.controller;


import com.abhi.synapster.inventory.constants.ApplicationConstants;
import com.abhi.synapster.inventory.constants.Status;
import com.abhi.synapster.inventory.constants.UrlConstants;
import com.abhi.synapster.inventory.dto.DocumentDTO;
import com.abhi.synapster.inventory.exceptions.entities.ServerException;
import com.abhi.synapster.inventory.model.rest.ResponseEntityBuilder;
import com.abhi.synapster.inventory.model.rest.RestApiResponse;
import com.abhi.synapster.inventory.service.DocumentService;
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
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping(value = UrlConstants.Document.DOCUMENT_ADD, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<DocumentDTO> requestDto, String contentRefId) {
        List<DocumentDTO> response = documentService.addAll(requestDto, Long.parseLong(contentRefId));
        if (CollectionUtils.isEmpty(response))
            throw new ServerException().new InternalError("Internal error occurred.");
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @PutMapping(value = UrlConstants.Document.DOCUMENT_UPDATE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody DocumentDTO documentDTO) {
        DocumentDTO response = documentService.update(documentDTO);
        if (response == null) throw new ServerException().new InternalError("Internal error occurred.");
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @DeleteMapping(value = UrlConstants.Document.DOCUMENT_DELETE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@RequestBody DocumentDTO documentDTO) {
        documentService.delete(documentDTO);
        return ResponseEntityBuilder.getBuilder(HttpStatus.NO_CONTENT).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, "Resource has been removed.");
    }

    @GetMapping(value = UrlConstants.Document.DOCUMENT_GET_BY_REF_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> getByRefId(@PathVariable String documentRefId) {
        DocumentDTO response = documentService.getByRefId(Long.parseLong(documentRefId));
        if (response == null) throw new ServerException().new InternalError("Internal error occurred.");
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }

    @GetMapping(value = UrlConstants.Document.DOCUMENT_GET_BY_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> getByName(@RequestParam String name) {
        DocumentDTO response = documentService.getByName(name);
        if (response == null) throw new ServerException().new InternalError("Internal error occurred.");
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
