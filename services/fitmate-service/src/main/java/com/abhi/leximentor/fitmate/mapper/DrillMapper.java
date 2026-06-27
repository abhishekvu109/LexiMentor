package com.abhi.leximentor.fitmate.mapper;

import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Routine;
import org.mapstruct.*;

/**
 * MapStruct mapper for {@link Drill} ↔ {@link DrillDTO}.
 *
 * <p>Replaces {@code FitmateServiceUtil.DrillUtil}.</p>
 *
 * <p><strong>Note on {@code routineObj}:</strong> The {@link Drill} entity holds
 * both a plain {@code long routine} (refId) and a {@link Routine} object
 * ({@code routineObj}).  The mapper only sets {@code routine} (the Long).
 * {@code routineObj} must be wired by the calling service after the entity is
 * created — exactly as the original builder did.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {FitmateMapperHelper.class, ExerciseMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DrillMapper {

    // -----------------------------------------------------------------------
    // Entity → DTO
    // -----------------------------------------------------------------------

    /**
     * Maps a persisted {@link Drill} to a DTO.
     *
     * <ul>
     *   <li>{@code refId}        — long → String.</li>
     *   <li>{@code routine}      — long refId → String.</li>
     *   <li>{@code exercise}     — mapped via {@link ExerciseMapper#toDto(com.abhi.leximentor.fitmate.entities.Exercise)}.</li>
     *   <li>{@code creationDate} — mapped from {@code crtnDate}.</li>
     * </ul>
     */
    @Mapping(target = "refId",        source = "refId",   qualifiedByName = "longToString")
    @Mapping(target = "routine",      source = "routine", qualifiedByName = "longToString")
    @Mapping(target = "exercise",     source = "exercise")   // ExerciseMapper.toDto() auto-selected
    @Mapping(target = "creationDate", source = "crtnDate")
    DrillDTO toDto(Drill entity);

    // -----------------------------------------------------------------------
    // DTO → Entity  (no Exercise / Routine context)
    // -----------------------------------------------------------------------

    /**
     * Creates a bare {@link Drill} from a DTO — without exercise or routine.
     * The calling service must set {@code exercise}, {@code routine}, and
     * {@code routineObj} manually.
     */
    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "uuid",        expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId",       expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.refId())")
    @Mapping(target = "exercise",    ignore = true)
    @Mapping(target = "routine",     ignore = true)
    @Mapping(target = "routineObj",  ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "crtnDate",    ignore = true)
    @Mapping(target = "lastUpdDate", ignore = true)
    Drill toEntity(DrillDTO dto);

    // -----------------------------------------------------------------------
    // DTO → Entity  (Exercise + Routine context provided)
    // -----------------------------------------------------------------------

    /**
     * Creates a {@link Drill} and wires the supplied {@link Exercise} and the
     * {@code refId} of the supplied {@link Routine}.
     *
     * <p>{@code routineObj} is intentionally left {@code null} here — the
     * service sets it via {@code drill.setRoutineObj(routine)} to avoid a
     * circular reference during the flush.</p>
     */
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "uuid",         expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId",        expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.refId())")
    @Mapping(target = "exercise",     source = "exercise")
    @Mapping(target = "routine",      source = "routine.refId")   // stores the Long refId
    @Mapping(target = "routineObj",   ignore = true)              // set by service: drill.setRoutineObj(routine)
    @Mapping(target = "description",  ignore = true)
    @Mapping(target = "crtnDate",     ignore = true)
    @Mapping(target = "lastUpdDate",  ignore = true)
    // Disambiguate fields that exist on both DrillDTO and Exercise
    @Mapping(target = "unit",         source = "dto.unit")
    @Mapping(target = "burntCalories", source = "dto.burntCalories")
    Drill toEntity(DrillDTO dto, Exercise exercise, Routine routine);
}
