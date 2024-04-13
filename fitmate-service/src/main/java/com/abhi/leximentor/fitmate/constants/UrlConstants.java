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

    public static class ExerciseUrl {
        public static final String EXERCISE_ADD = "/api/fitmate/exercises/exercise";
        public static final String EXERCISE_UPDATE = "/api/fitmate/exercises/exercise";
        public static final String EXERCISE_DELETE = "/api/fitmate/exercises/exercise";
        public static final String EXERCISE_GET = "/api/fitmate/exercises/exercise";
        public static final String EXERCISE_GET_REF_ID = "/api/fitmate/exercises/exercise/{exerciseRefId}";
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
        public static final String ROUTINE_ADD = "/api/fitmate/routines/routine";
        public static final String ROUTINE_UPDATE = "/api/fitmate/routines/routine";
        public static final String ROUTINE_DELETE = "/api/fitmate/routines/routine";
        public static final String ROUTINE_GET_REF_ID = "/api/fitmate/routines/routine/{routineRefId}";
    }
}
