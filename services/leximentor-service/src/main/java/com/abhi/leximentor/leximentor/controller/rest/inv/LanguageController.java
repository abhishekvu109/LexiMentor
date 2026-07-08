package com.abhi.leximentor.leximentor.controller.rest.inv;

import com.abhi.leximentor.leximentor.constants.ApplicationConstants;
import com.abhi.leximentor.leximentor.dto.inv.LanguageDTO;
import com.abhi.leximentor.leximentor.model.rest.ResponseEntityBuilder;
import com.abhi.leximentor.leximentor.model.rest.RestApiResponse;
import com.abhi.leximentor.leximentor.service.inv.LanguageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping({"/api/v1/leximentor/languages", "/api/leximentor/inventory/language"})
public class LanguageController {
    private final LanguageService languageService;

    @PostMapping(produces = ApplicationConstants.MediaType.APPLICATION_JSON, consumes = ApplicationConstants.MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<RestApiResponse> addLanguage(@Valid @RequestBody LanguageDTO dto) {
        log.info("Language create requested. language={}", dto == null ? null : dto.getLanguage());
        LanguageDTO response = languageService.add(dto);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION, response);
    }
}
