package com.abhi.saarthi.auth.service.impl;

import com.abhi.saarthi.auth.dto.UserRoleDTO;
import com.abhi.saarthi.auth.entity.UserRole;
import com.abhi.saarthi.auth.repository.UserRoleRepository;
import com.abhi.saarthi.auth.service.ServiceUtil;
import com.abhi.saarthi.auth.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public List<UserRoleDTO> save(List<UserRoleDTO> dto) {
        List<UserRole> roles = dto.stream().map(ServiceUtil.UserRoleServiceUtil::toEntity).toList();
        roles = userRoleRepository.saveAll(roles);
        return roles.stream().map(ServiceUtil.UserRoleServiceUtil::toDTO).toList();
    }

    @Override
    @Transactional
    public UserRoleDTO update(UserRoleDTO dto) {
        UserRole role = userRoleRepository.findByRefId(Long.parseLong(dto.getRefId())).orElseThrow(() -> new RuntimeException("Invalid user role"));
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role = userRoleRepository.save(role);
        return ServiceUtil.UserRoleServiceUtil.toDTO(userRoleRepository.save(role));
    }

    @Override
    public void delete(UserRoleDTO dto) {
        UserRole role = userRoleRepository.findByRefId(Long.parseLong(dto.getRefId())).orElseThrow(() -> new RuntimeException("Invalid user role"));
        userRoleRepository.delete(role);
    }

    @Override
    public List<UserRoleDTO> findAll() {
        List<UserRole> roles = userRoleRepository.findAll();
        return roles.stream().map(ServiceUtil.UserRoleServiceUtil::toDTO).toList();
    }

    @Override
    public UserRoleDTO findByRefId(long refId) {
        UserRole role = userRoleRepository.findByRefId(refId).orElseThrow(() -> new RuntimeException("Invalid user role"));
        return ServiceUtil.UserRoleServiceUtil.toDTO(userRoleRepository.save(role));
    }

    @Override
    public UserRoleDTO findByName(String name) {
        UserRole role = userRoleRepository.findByNameIgnoreCase(name).orElseThrow(() -> new RuntimeException("Invalid user role"));
        return ServiceUtil.UserRoleServiceUtil.toDTO(userRoleRepository.save(role));
    }
}
