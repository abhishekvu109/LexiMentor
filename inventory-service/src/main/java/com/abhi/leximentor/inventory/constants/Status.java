package com.abhi.leximentor.inventory.constants;

public class Status {
    public static class ApplicationStatus {
        public static final int ACTIVE = 1;
        public static final int INACTIVE = 0;

        public static String getStatus(int status) {
            return (status == ACTIVE) ? "Active" : "Inactive";
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
                case COMPLETED -> "Completed";
                default -> "Evaluated";
            };
        }
    }
}
