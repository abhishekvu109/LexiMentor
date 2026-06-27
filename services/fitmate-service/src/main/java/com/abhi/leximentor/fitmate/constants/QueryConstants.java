package com.abhi.leximentor.fitmate.constants;

public class QueryConstants {
    public static final String ROUTINE_DRILL_VIEW = """
            select training.name AS training_name,
                   routine.routine_date AS routine_date,
                   exercise.name AS exercise_name,
                   drill.unit AS unit,
                   drill.measurement AS measurement,
                   drill.measurement_unit AS measurement_unit,
                   drill.repetition AS repetition,
                   bp.name AS body_part
            from fitmate_training_metadata training,
                 fitmate_routine routine,
                 fitmate_routine_drill drill,
                 fitmate_exercise exercise,
                 fitmate_body_parts bp
            where routine.username = :username
              and training.id = routine.training_id
              and routine.id = drill.routine_id2
              and drill.exercise_id = exercise.id
              and exercise.target_body_part = bp.id
            order by routine.routine_date desc;
            """;

    public static class ExerciseQueries {
        public static final String GET_EXERCISES_BY_NAME = "SELECT * FROM fitmate_exercise WHERE upper(name) = :name";
    }

    public static class Training {
        public static final String GET_TRAINING_BY_NAME = "select * from fitmate_training_metadata where lower(name)=lower(:name)";
    }


}
