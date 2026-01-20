package com.abhi.saarthi.auth.service;

import com.abhi.saarthi.auth.constant.Status;
import com.abhi.saarthi.auth.dto.AppUser;
import com.abhi.saarthi.auth.dto.UserRoleDTO;
import com.abhi.saarthi.auth.entity.User;
import com.abhi.saarthi.auth.entity.UserRole;
import com.abhi.saarthi.auth.util.KeyGeneratorUtil;

import java.util.stream.Collectors;

public class ServiceUtil {
    public static class UserService {
        public static AppUser buildDTO(User user) {
            return AppUser.builder()
                    .refId(String.valueOf(user.getRefId()))
                    .uuid(user.getUuid())
                    .username(user.getUsername())
                    .status(Status.ApplicationStatus.getStatusStr(user.getStatus()))
                    .role(user.getRole())
                    .roles(user.getRoles().stream().map(UserRoleServiceUtil::toDTO).collect(Collectors.toSet()))
                    .build();
        }

    }

    public static class UserRoleServiceUtil {
        public static UserRoleDTO toDTO(UserRole role) {
            return UserRoleDTO.builder()
                    .uuid(role.getUuid())
                    .refId(String.valueOf(role.getRefId()))
                    .name(role.getName())
                    .status(Status.ApplicationStatus.getStatusStr(role.getStatus()))
                    .description(role.getDescription())
                    .build();
        }

        public static UserRole toEntity(UserRoleDTO dto) {
            return UserRole.builder()
                    .uuid(KeyGeneratorUtil.uuid())
                    .refId(KeyGeneratorUtil.refId())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .status(Status.ApplicationStatus.ACTIVE)
                    .build();
        }
    }
}
