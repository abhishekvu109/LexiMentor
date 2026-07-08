package com.abhi.leximentor.leximentor.mapper;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public interface EntityMapper<D, E> {
    D toDto(E entity);

    E toEntity(D dto);

    default List<D> toDtoList(List<E> entities) {
        return mapList(entities, this::toDto);
    }

    default List<E> toEntityList(List<D> dtos) {
        return mapList(dtos, this::toEntity);
    }

    private <T, R> List<R> mapList(List<T> input, Function<T, R> mapper) {
        if (input == null || input.isEmpty()) {
            return List.of();
        }
        return input.stream().filter(Objects::nonNull).map(mapper).toList();
    }
}
