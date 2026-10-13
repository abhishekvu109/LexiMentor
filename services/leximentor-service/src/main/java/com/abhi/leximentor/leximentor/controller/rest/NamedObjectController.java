package com.abhi.leximentor.leximentor.controller.rest;

import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.dto.NamedObjectDTO;
import com.abhi.leximentor.leximentor.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
import com.abhi.leximentor.leximentor.service.NamedObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping({"/api/v1/leximentor/named-objects", "/api/leximentor/v1/named/object"})
public class NamedObjectController {
    private final NamedObjectService namedObjectService;

    @PostMapping(produces = ApplicationConstants.MediaType.APPLICATION_JSON, consumes = ApplicationConstants.MediaType.APPLICATION_JSON)
    public ResponseEntity<RestApiResponse> addAll(@RequestBody List<NamedObjectDTO> dtos) {
        log.info("NamedObject create requested. count={}", dtos == null ? 0 : dtos.size());
        List<NamedObjectDTO> response = namedObjectService.addAll(dtos);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
