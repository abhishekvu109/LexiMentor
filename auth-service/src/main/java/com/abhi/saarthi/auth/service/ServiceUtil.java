package com.abhi.saarthi.auth.service;

import com.abhi.saarthi.auth.constant.Status;
import com.abhi.saarthi.auth.dto.AppUser;
import com.abhi.saarthi.auth.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ServiceUtil {
    public static class UserService{
        public static AppUser buildDTO(User user){
            return AppUser.builder()
                    .username(user.getUsername())
                    .status(Status.ApplicationStatus.getStatusStr(user.getStatus()))
                    .role(user.getRole())
                    .build();
        }

    }
}
