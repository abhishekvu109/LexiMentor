package com.abhi.leximentor.inventory.constants;

public class Status {
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    public static String getStatus(int status) {
        return (status == ACTIVE) ? "Active" : "Inactive";
    }
}
