package com.abhi.leximentor.leximentor.service.base;

import com.abhi.leximentor.leximentor.exceptions.entities.InvalidDTOException;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractApplicationService {

    protected <T> T requireEntity(T entity, String message) {
        if (entity == null) {
            throw new EntityNotFoundException(message);
        }
        return entity;
    }

    protected long parseRefId(String value, String fieldName) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new InvalidDTOException(fieldName + " must be a valid number.");
        }
    }

    protected <T, R> List<R> mapList(List<T> input, Function<T, R> mapper) {
        if (input == null || input.isEmpty()) {
            return List.of();
        }
        return input.stream()
                .filter(Objects::nonNull)
                .map(mapper)
                .toList();
    }
}
