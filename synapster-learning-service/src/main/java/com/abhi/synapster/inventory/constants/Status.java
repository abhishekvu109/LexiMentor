package com.abhi.synapster.inventory.constants;

public class Status {
    public static class ApplicationStatus {
        public static final int ACTIVE = 1;
        public static final int INACTIVE = 0;

        public static final String ACTIVE_STR = "Active";
        public static final String INACTIVE_STR = "Inactive";

        public static String toString(int status) {
            return (status == ACTIVE) ? "Active" : "Inactive";
        }

        public static int getStatus(String status) {
            return (status.equalsIgnoreCase(ACTIVE_STR)) ? ACTIVE : INACTIVE;
        }
    }
}
