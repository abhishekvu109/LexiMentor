package com.abhi.saarthi.auth.controller;

import com.abhi.saarthi.auth.constant.ApplicationConstants;
import com.abhi.saarthi.auth.dto.UserRoleDTO;
import com.abhi.saarthi.auth.model.ResponseEntityBuilder;
import com.abhi.saarthi.auth.model.RestApiResponse;
import com.abhi.saarthi.auth.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api/auth/v1/roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping(value = "/role", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> add(@RequestBody List<UserRoleDTO> dto) {
        List<UserRoleDTO> response = userRoleService.save(dto);
        return ResponseEntityBuilder.getBuilder(HttpStatus.CREATED).successResponse(ApplicationConstants.REQUEST_SUCCESS_CODE, response);
    }

    @PutMapping(value = "/role", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<RestApiResponse> update(@RequestBody UserRoleDTO dto) {
        UserRoleDTO response = userRoleService.update(dto);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_CODE, response);
    }

    @DeleteMapping(value = "/role/{refId}")
    public @ResponseBody ResponseEntity<RestApiResponse> delete(@PathVariable String refId) {
        UserRoleDTO dto = UserRoleDTO.builder().refId(refId).build();
        userRoleService.delete(dto);
        return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_CODE, ApplicationConstants.REQUEST_SUCCESS_DESCRIPTION);
    }

    @GetMapping(value = "/role")
    public @ResponseBody ResponseEntity<RestApiResponse> get(@RequestParam(required = false) String refId, @RequestParam(required = false) String name) {
        if (StringUtils.isAllEmpty(refId, name)) {
            List<UserRoleDTO> userRoleDTOList = userRoleService.findAll();
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_CODE, userRoleDTOList);
        } else if (StringUtils.isNotEmpty(refId)) {
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_CODE, userRoleService.findByRefId(Long.parseLong(refId)));
        } else {
            return ResponseEntityBuilder.getBuilder(HttpStatus.OK).successResponse(ApplicationConstants.REQUEST_SUCCESS_CODE, userRoleService.findByName(name));
        }
    }


}
