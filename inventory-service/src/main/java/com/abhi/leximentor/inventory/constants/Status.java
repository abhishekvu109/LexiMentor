package com.abhi.leximentor.inventory.constants;

import org.apache.commons.lang3.StringUtils;

public class Status {
    public static class ApplicationStatus {
        public static final int ACTIVE = 1;
        public static final int INACTIVE = 0;

        public static final String ACTIVE_STR = "ACTIVE";
        public static final String INACTIVE_STR = "INACTIVE";

        public static String getStatusStr(int status) {
            return (status == ACTIVE) ? "Active" : "Inactive";
        }

        public static int getStatus(String status) {
            return StringUtils.equalsIgnoreCase(status, ACTIVE_STR) ? ACTIVE : INACTIVE;
        }
    }

    public static class DrillChallenge {
        public static final int NOT_INITIATED = 0;
        public static final int IN_PROGRESS = 1;
        public static final int COMPLETED = 2;
        public static final int EVALUATED = 3;

        public static String getStatus(int status) {
            return switch (status) {
                case NOT_INITIATED -> "Not Initiated";
                case IN_PROGRESS -> "In Progress";
                default -> "Completed";
            };
        }

        public static String getEvaluationStatus(int status) {
            return switch (status) {
                case NOT_INITIATED -> "Not Evaluated";
                case IN_PROGRESS -> "In Progress";
                default -> "Evaluated";
            };
        }
    }

}
