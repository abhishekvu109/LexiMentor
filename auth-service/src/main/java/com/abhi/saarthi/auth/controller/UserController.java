package com.abhi.saarthi.auth.controller;

import com.abhi.saarthi.auth.constant.ApplicationConstants;
import com.abhi.saarthi.auth.dto.AppUser;
import com.abhi.saarthi.auth.model.ResponseEntityBuilder;
import com.abhi.saarthi.auth.model.RestApiResponse;
import com.abhi.saarthi.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/api/auth/v1/user")
public class UserController {

    private final UserService userService;



    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> findAll() {
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_CODE, userService.findAllUsers());
    }
}
