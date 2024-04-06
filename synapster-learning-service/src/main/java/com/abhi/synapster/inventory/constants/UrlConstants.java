package com.abhi.synapster.inventory.constants;

public class UrlConstants {
    public static class Subject {
        public static final String SUBJECT_ADD = "/api/synapster/subjects/subject";
        public static final String SUBJECT_UPDATE = "/api/synapster/subjects/subject";
        public static final String SUBJECT_DELETE = "/api/synapster/subjects/subject";
        public static final String SUBJECT_GET_BY_REF_ID = "/api/synapster/subjects/subject/{subjectRefId}";
    }

    public static class Topic {
        public static final String TOPIC_ADD = "/api/synapster/topics/topic";
        public static final String TOPIC_UPDATE = "/api/synapster/topics/topic";
        public static final String TOPIC_DELETE = "/api/synapster/topics/topic";
        public static final String TOPIC_GET_BY_NAME = "/api/synapster/topics/topic";
        public static final String TOPIC_GET_BY_REF_ID = "/api/synapster/topics/topic/{topicRefId}";
        public static final String TOPIC_GET_BY_SUBJECT_REF_ID = "/api/synapster/topics/{subjectRefId}";
    }

    public static class Content {
        public static final String CONTENT_ADD = "/api/synapster/contents/content";
        public static final String CONTENT_UPDATE = "/api/synapster/contents/content";
        public static final String CONTENT_DELETE = "/api/synapster/contents/content";
        public static final String CONTENT_GET_BY_REF_ID = "/api/synapster/contents/content/{contentRefId}";
        public static final String CONTENT_GET_BY_TOPIC = "/api/synapster/contents/{topicRefId}";

    }

    public static class Document {
        public static final String DOCUMENT_ADD = "/api/synapster/documents/document";
        public static final String DOCUMENT_UPDATE = "/api/synapster/documents/document";
        public static final String DOCUMENT_DELETE = "/api/synapster/documents/document";
        public static final String DOCUMENT_GET_BY_NAME = "/api/synapster/documents/document";
        public static final String DOCUMENT_GET_BY_REF_ID = "/api/synapster/documents/document/{documentRefId}";

    }

}
