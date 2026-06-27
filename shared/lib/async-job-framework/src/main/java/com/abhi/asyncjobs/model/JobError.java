package com.abhi.asyncjobs.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public final class JobError {
    private final String type;
    private final String message;
    private final String stackTrace;

    private JobError(String type, String message, String stackTrace) {
        this.type = type;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public static JobError of(String type, String message, String stackTrace) {
        return new JobError(type, message, stackTrace);
    }

    public static JobError from(Throwable throwable) {
        Objects.requireNonNull(throwable, "throwable must not be null");
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return new JobError(
            throwable.getClass().getName(),
            throwable.getMessage(),
            sw.toString()
        );
    }

    public String type() {
        return type;
    }

    public String getType() {
        return type;
    }

    public String message() {
        return message;
    }

    public String getMessage() {
        return message;
    }

    public String stackTrace() {
        return stackTrace;
    }

    public String getStackTrace() {
        return stackTrace;
    }
}
