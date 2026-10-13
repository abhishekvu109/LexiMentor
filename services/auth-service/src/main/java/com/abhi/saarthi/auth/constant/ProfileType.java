package com.abhi.saarthi.auth.constant;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public enum ProfileType {
    ADMIN("ADMIN"),
    MEMBER("MEMBER");

    private final String value;

    public static ProfileType parse(String value) {
        if (StringUtils.equalsIgnoreCase(value, ADMIN.value))
            return ADMIN;
        else if (StringUtils.equalsIgnoreCase(value, MEMBER.value))
            return MEMBER;
        else
            return MEMBER;
    }

    public static String parse(ProfileType type) {
        if (type == ADMIN)
            return ADMIN.value;
        else
            return MEMBER.value;
    }

}
