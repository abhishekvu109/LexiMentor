package com.abhi.saarthi.auth.service;

import com.abhi.saarthi.auth.dto.UserRoleDTO;

import java.util.List;

public interface UserRoleService {
    List<UserRoleDTO> save(List<UserRoleDTO> dto);
    UserRoleDTO update(UserRoleDTO dto);
    void delete(UserRoleDTO dto);
    List<UserRoleDTO> findAll();
    UserRoleDTO findByRefId(long refId);
    UserRoleDTO findByName(String name);
}
