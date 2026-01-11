package com.abhi.saarthi.cashflow.constants;

public class QueryConstants {
    public static class ExerciseQueries {
        public static final String GET_EXERCISES_BY_NAME = "SELECT * FROM fitmate_exercise WHERE upper(name) = :name";
    }

    public static class Training {
        public static final String GET_TRAINING_BY_NAME = "select * from fitmate_training_metadata where lower(name)=lower(:name)";
    }
}
