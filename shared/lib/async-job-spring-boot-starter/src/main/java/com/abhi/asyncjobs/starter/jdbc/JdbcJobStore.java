package com.abhi.asyncjobs.starter.jdbc;

import com.abhi.asyncjobs.model.JobError;
import com.abhi.asyncjobs.model.JobSnapshot;
import com.abhi.asyncjobs.model.JobStatus;
import com.abhi.asyncjobs.store.JobState;
import com.abhi.asyncjobs.store.JobStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class JdbcJobStore implements JobStore {
    private static final TypeReference<Map<String, String>> STRING_MAP_REF = new TypeReference<>() {
    };

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final String tableName;
    private final ConcurrentMap<String, JobState> runtimeStates = new ConcurrentHashMap<>();

    public JdbcJobStore(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper, String tableName) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.tableName = tableName;
    }

    @Override
    public void save(JobState state) {
        runtimeStates.put(state.jobId(), state);
        JobSnapshot snapshot = state.snapshot();

        int updated = jdbcTemplate.update("""
                UPDATE %s SET
                    job_type = ?,
                    status = ?,
                    attempt = ?,
                    payload_json = ?,
                    result_json = ?,
                    metadata_json = ?,
                    error_type = ?,
                    error_message = ?,
                    error_stack = ?,
                    created_at = ?,
                    updated_at = ?,
                    started_at = ?,
                    completed_at = ?
                WHERE job_id = ?
                """.formatted(tableName),
            snapshot.jobType(),
            snapshot.status().name(),
            snapshot.attempt(),
            toJson(state.request().payload()),
            toJson(snapshot.result()),
            toJson(snapshot.metadata()),
            snapshot.error() == null ? null : snapshot.error().type(),
            snapshot.error() == null ? null : snapshot.error().message(),
            snapshot.error() == null ? null : snapshot.error().stackTrace(),
            ts(snapshot.createdAt()),
            ts(snapshot.updatedAt()),
            ts(snapshot.startedAt()),
            ts(snapshot.completedAt()),
            snapshot.jobId()
        );

        if (updated == 0) {
            jdbcTemplate.update("""
                    INSERT INTO %s
                    (job_id, job_type, status, attempt, payload_json, result_json, metadata_json, error_type, error_message, error_stack, created_at, updated_at, started_at, completed_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """.formatted(tableName),
                snapshot.jobId(),
                snapshot.jobType(),
                snapshot.status().name(),
                snapshot.attempt(),
                toJson(state.request().payload()),
                toJson(snapshot.result()),
                toJson(snapshot.metadata()),
                snapshot.error() == null ? null : snapshot.error().type(),
                snapshot.error() == null ? null : snapshot.error().message(),
                snapshot.error() == null ? null : snapshot.error().stackTrace(),
                ts(snapshot.createdAt()),
                ts(snapshot.updatedAt()),
                ts(snapshot.startedAt()),
                ts(snapshot.completedAt())
            );
        }
    }

    @Override
    public Optional<JobState> findState(String jobId) {
        return Optional.ofNullable(runtimeStates.get(jobId));
    }

    @Override
    public Optional<JobSnapshot> findSnapshot(String jobId) {
        return jdbcTemplate.query("SELECT * FROM " + tableName + " WHERE job_id = ?",
                (rs, rowNum) -> mapSnapshot(rs),
                jobId)
            .stream()
            .findFirst();
    }

    @Override
    public Collection<JobSnapshot> listSnapshots() {
        return jdbcTemplate.query("SELECT * FROM " + tableName + " ORDER BY created_at DESC", (rs, rowNum) -> mapSnapshot(rs));
    }

    private JobSnapshot mapSnapshot(ResultSet rs) throws SQLException {
        String errorType = rs.getString("error_type");
        String errorMessage = rs.getString("error_message");
        String errorStack = rs.getString("error_stack");

        JobError error = errorType == null ? null : reconstructError(errorType, errorMessage, errorStack);
        Map<String, String> metadata = fromJson(rs.getString("metadata_json"), STRING_MAP_REF);
        Object result = fromJson(rs.getString("result_json"), Object.class);

        return new JobSnapshot(
            rs.getString("job_id"),
            rs.getString("job_type"),
            JobStatus.valueOf(rs.getString("status")),
            rs.getInt("attempt"),
            result,
            error,
            toInstant(rs.getTimestamp("created_at")),
            toInstant(rs.getTimestamp("updated_at")),
            toInstant(rs.getTimestamp("started_at")),
            toInstant(rs.getTimestamp("completed_at")),
            metadata
        );
    }

    private JobError reconstructError(String type, String message, String stack) {
        return JobError.of(type, message, stack);
    }

    private Timestamp ts(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }

    private Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize async job payload", ex);
        }
    }

    private <T> T fromJson(String json, Class<T> targetType) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, targetType);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize async job payload", ex);
        }
    }

    private <T> T fromJson(String json, TypeReference<T> targetType) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, targetType);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize async job payload", ex);
        }
    }
}
