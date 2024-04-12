package com.abhi.leximentor.fitmate.constants;

public class Status {
    public static class ApplicationStatus {
        public static final int ACTIVE = 1;
        public static final int INACTIVE = 0;

        public static String getStatusStr(int status) {
            return (status == ACTIVE) ? "Active" : "Inactive";
        }

        public static int getStatus(String status) {
            return (status.equalsIgnoreCase("active")) ? ACTIVE : INACTIVE;
        }
    }
}
