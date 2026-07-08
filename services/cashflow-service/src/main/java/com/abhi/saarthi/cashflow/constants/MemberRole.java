package com.abhi.saarthi.cashflow.constants;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public enum MemberRole {
    ADMIN("ADMIN"),
    MEMBER("MEMBER"),
    VIEWER("VIEWER");

    private String value;

    public static String parse(MemberRole role) {
        if (role == ADMIN)
            return ADMIN.value;
        else if (role == MEMBER)
            return MEMBER.value;
        else
            return VIEWER.value;
    }

    public static MemberRole parse(String role) {
        if (StringUtils.equalsIgnoreCase(role, ADMIN.value))
            return ADMIN;
        else if (StringUtils.equalsIgnoreCase(role, MEMBER.value))
            return MEMBER;
        else
            return VIEWER;
    }

}
