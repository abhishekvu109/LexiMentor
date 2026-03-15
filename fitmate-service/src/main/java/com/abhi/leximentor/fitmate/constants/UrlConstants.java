package com.abhi.leximentor.fitmate.constants;

public class UrlConstants {
    public static class BodyPartsUrl {
        public static final String BODY_PARTS_ADD = "/api/fitmate/bodyparts/bodypart";
        public static final String BODY_PARTS_GET_ALL = "/api/fitmate/bodyparts";
        public static final String BODY_PARTS_UPDATE = "/api/fitmate/bodyparts/bodypart";
        public static final String BODY_PARTS_DELETE = "/api/fitmate/bodyparts/bodypart";
        public static final String BODY_PARTS_GET_BY_NAME = "/api/fitmate/bodyparts/bodypart";
        public static final String BODY_PARTS_GET_REF_ID = "/api/fitmate/bodyparts/bodypart/{bodyPartRefId}";
    }

    public static class TrainingMetadataUrl {
        public static final String TRAINING_METADATA_ADD = "/api/fitmate/trainings/training";
        public static final String TRAINING_METADATA_GET = "/api/fitmate/trainings/training";
        public static final String TRAINING_METADATA_GET_ALL = "/api/fitmate/trainings";
        public static final String TRAINING_METADATA_UPDATE = "/api/fitmate/trainings/training";
        public static final String TRAINING_METADATA_DELETE = "/api/fitmate/trainings/training";
        public static final String TRAINING_METADATA_GET_REF_ID = "/api/fitmate/trainings/training/{trainingRefId}";
    }

    public static class RoutineUrl {
        public static final String BASE_URL = "/api/fitmate/routines/routine";
    }

    public static class Muscle {
        public static final String MUSCLE_ADD = "/api/fitmate/muscles/muscle";
        public static final String MUSCLE_FIND_ALL = "/api/fitmate/muscles";
        public static final String MUSCLE_DELETE = "/api/fitmate/muscles/muscle";
    }

    public static class Drill {
        public static final String DRILL_FIND_BY_EXERCISE_NAME = "/api/fitmate/drill/{exerciseName}";
        public static final String DRILL_UPDATE = "/api/fitmate/drills/drill";

    }

    public static class Analytics {
        public static final String ANALYTICS_BASE_URI = "/api/fitmate/analytics";
        public static final String ANALYTICS_OVERALL = "/overall";
    }

    public static class Recommendation {
        public static final String BASE_URL = "/api/fitmate/recommendations";
        public static final String RECOMMEND = "/recommend";
    }
}
