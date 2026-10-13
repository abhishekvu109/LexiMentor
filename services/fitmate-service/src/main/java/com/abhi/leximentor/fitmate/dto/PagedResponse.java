package com.abhi.leximentor.fitmate.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Generic pagination wrapper returned by all list endpoints.
 *
 * <p>Use the static factory methods to build from a Spring Data {@link Page}:
 * <pre>{@code
 *   // Entity page → DTO page (mapper function)
 *   PagedResponse.of(exerciseRepository.findAll(pageable), exerciseMapper::toDto)
 *
 *   // Already-mapped DTO page
 *   PagedResponse.of(trainingRepository.findAll(pageable).map(trainingMapper::toDto))
 *
 *   // In-memory list slice (for small, already-loaded lists)
 *   PagedResponse.ofList(dtoList, page, size)
 * }</pre>
 */
@Data
@Builder
public class PagedResponse<T> {

    /** The items on the current page. */
    private List<T> content;

    /** Zero-based current page index. */
    private int page;

    /** Number of items requested per page. */
    private int size;

    /** Total number of items across all pages. */
    private long totalElements;

    /** Total number of pages. */
    private int totalPages;

    /** {@code true} if this is the first page. */
    private boolean first;

    /** {@code true} if this is the last page. */
    private boolean last;

    // -------------------------------------------------------------------------
    // Factory methods
    // -------------------------------------------------------------------------

    /**
     * Builds a {@code PagedResponse<T>} directly from a Spring Data {@link Page<T>}.
     * Use when the page content type already matches the desired response type.
     */
    public static <T> PagedResponse<T> of(Page<T> springPage) {
        return PagedResponse.<T>builder()
                .content(springPage.getContent())
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .first(springPage.isFirst())
                .last(springPage.isLast())
                .build();
    }

    /**
     * Builds a {@code PagedResponse<T>} from a {@link Page<S>} by applying a mapping
     * function to each element.  Use when the repository returns entities and you need DTOs.
     *
     * @param springPage the raw entity page from the repository
     * @param mapper     entity → DTO conversion function (e.g. {@code exerciseMapper::toDto})
     */
    public static <S, T> PagedResponse<T> of(Page<S> springPage, Function<S, T> mapper) {
        return PagedResponse.<T>builder()
                .content(springPage.getContent().stream().map(mapper).toList())
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .first(springPage.isFirst())
                .last(springPage.isLast())
                .build();
    }

    /**
     * Builds a {@code PagedResponse<T>} by slicing an already-loaded in-memory list.
     * Prefer DB-level pagination via {@link #of(Page)} where possible; use this only
     * for small, pre-loaded collections (e.g. results of in-memory joins).
     *
     * @param list full list of items (all pages)
     * @param page zero-based page index
     * @param size page size
     */
    public static <T> PagedResponse<T> ofList(List<T> list, int page, int size) {
        int total = list.size();
        int totalPages = (size > 0) ? (int) Math.ceil((double) total / size) : 1;
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        return PagedResponse.<T>builder()
                .content(list.subList(from, to))
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages(totalPages)
                .first(page == 0)
                .last(to >= total)
                .build();
    }
}
