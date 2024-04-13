package com.abhi.leximentor.fitmate.constants;

public class QueryConstants {
    public static class ExerciseQueries {
        public static final String GET_EXERCISES_BY_NAME = "SELECT * FROM fitmate_exercise WHERE upper(trim(name)) = :name";
    }
}
