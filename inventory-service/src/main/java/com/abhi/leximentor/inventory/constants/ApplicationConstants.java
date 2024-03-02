package com.abhi.leximentor.inventory.constants;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

public class ApplicationConstants {


    public static final String KAFKA_TOPIC = "word_topic";
    public static final String KAFKA_GROUP = "word-group-1";
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAILURE = 1;

    public static final String REQUEST_SUCCESS_CODE = "000";
    public static final String REQUEST_SUCCESS_DESCRIPTION = "SUCCESS";
    public static final String REQUEST_FAILURE_DESCRIPTION = "FAILED";


    public static class MediaType {
        public static final String APPLICATION_JSON = APPLICATION_JSON_VALUE;
        public static final String APPLICATION_XML = APPLICATION_XML_VALUE;
    }

    public static class Status {
        public static final int ACTIVE = 1;
        public static final String ACTIVE_STRING = "Active";
        public static final int INACTIVE = 0;
        public static final String INACTIVE_STRING = "Inactive";
    }
}
