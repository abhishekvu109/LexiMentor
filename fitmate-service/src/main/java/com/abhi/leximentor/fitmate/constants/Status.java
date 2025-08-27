package com.abhi.leximentor.fitmate.constants;

import java.util.HashMap;
import java.util.Map;

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

    public static class RoutineStatus {
        public static final int NOT_STARTED = 0;
        public static final int IN_PROGRESS = 1;
        public static final int COMPLETED = 2;
        public static final int DISCARD = -1;

        static Map<Integer, String> intToStr = new HashMap<>();
        static Map<String, Integer> strToInt = new HashMap<>();

        static {
            intToStr.put(NOT_STARTED, "NOT_STARTED");
            intToStr.put(IN_PROGRESS, "IN_PROGRESS");
            intToStr.put(COMPLETED, "COMPLETED");
            intToStr.put(DISCARD, "DISCARD");

            strToInt.put("NOT_STARTED", NOT_STARTED);
            strToInt.put("IN_PROGRESS", IN_PROGRESS);
            strToInt.put("COMPLETED", COMPLETED);
            strToInt.put("DISCARD", DISCARD);
        }

        public static String toString(int status) {
            return intToStr.get(status);
        }

        public static int toInt(String status) {
            return strToInt.get(status.toUpperCase());
        }

    }
}
