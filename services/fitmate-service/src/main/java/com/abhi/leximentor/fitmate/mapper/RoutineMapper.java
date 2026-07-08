package com.abhi.leximentor.fitmate.mapper;

import com.abhi.leximentor.fitmate.dto.DrillDTO;
import com.abhi.leximentor.fitmate.dto.RoutineDTO;
import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Routine;
import com.abhi.leximentor.fitmate.entities.Training;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for {@link Routine} ↔ {@link RoutineDTO}.
 *
 * <p>Replaces {@code FitmateServiceUtil.RoutineUtil}.</p>
 *
 * <h3>Date-format variants</h3>
 * Two {@code toEntity} overloads exist because the legacy API accepted dates in
 * two different formats depending on the call site:
 * <ul>
 *   <li>{@link #toEntityWithDrills(RoutineDTO, Training, List)} — {@code MM/dd/yyyy}</li>
 *   <li>{@link #toEntity(RoutineDTO, Training)}                 — {@code yyyy-MM-dd}</li>
 * </ul>
 *
 * <h3>Status</h3>
 * New routines are always created with status {@code NOT_STARTED}; the DTO
 * status field is intentionally ignored on creation.
 */
@Mapper(
        componentModel = "spring",
        uses = {FitmateMapperHelper.class, TrainingMapper.class, DrillMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoutineMapper {

    // -----------------------------------------------------------------------
    // Entity → DTO  (auto-maps Drill list via DrillMapper)
    // -----------------------------------------------------------------------

    /**
     * Maps a full {@link Routine} (with its drills) to a DTO.
     *
     * <ul>
     *   <li>{@code key}         — mapped from {@code uuid}.</li>
     *   <li>{@code workoutDate} — converted from {@link java.time.LocalDate} to String.</li>
     *   <li>{@code status}      — int → routine-status String.</li>
     *   <li>{@code drills}      — each {@link Drill} is mapped via {@link DrillMapper#toDto(Drill)}.</li>
     * </ul>
     */
    @Mapping(target = "refId",        source = "refId",       qualifiedByName = "longToString")
    @Mapping(target = "key",          source = "uuid")
    @Mapping(target = "workoutDate",  source = "routineDate", qualifiedByName = "routineDateToString")
    @Mapping(target = "status",       source = "status",      qualifiedByName = "routineStatusToString")
    @Mapping(target = "training",     source = "training")    // TrainingMapper.toDto() auto-selected
    @Mapping(target = "drills",       source = "drills")      // DrillMapper.toDto() auto-selected for each element
    RoutineDTO toDto(Routine entity);

    /**
     * Variant that accepts pre-mapped {@link DrillDTO} objects.
     * Use this when the caller has already resolved the drills outside the
     * mapper (e.g. in {@code RoutineServiceImpl.search()}).
     */
    @Mapping(target = "refId",        source = "entity.refId",       qualifiedByName = "longToString")
    @Mapping(target = "key",          source = "entity.uuid")
    @Mapping(target = "workoutDate",  source = "entity.routineDate", qualifiedByName = "routineDateToString")
    @Mapping(target = "status",       source = "entity.status",      qualifiedByName = "routineStatusToString")
    @Mapping(target = "training",     source = "entity.training")
    @Mapping(target = "drills",       source = "drills")
    RoutineDTO toDto(Routine entity, List<DrillDTO> drills);

    // -----------------------------------------------------------------------
    // DTO → Entity  (without drills list — yyyy-MM-dd date format)
    // -----------------------------------------------------------------------

    /**
     * Creates a {@link Routine} without a drill list.
     * Used by {@code RoutineServiceImpl.add()} and the routine generator,
     * where drills are built and attached separately.
     *
     * <p>Date format: {@code yyyy-MM-dd} (ISO).</p>
     */
    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "uuid",             expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId",            expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.refId())")
    @Mapping(target = "status",           expression = "java(com.abhi.leximentor.fitmate.constants.Status.RoutineStatus.NOT_STARTED)")
    @Mapping(target = "routineDate",      source = "dto.workoutDate", qualifiedByName = "routineDateFromDashFormat")
    @Mapping(target = "training",         source = "training")
    @Mapping(target = "drills",           ignore = true)   // set by service after creation
    @Mapping(target = "description",      source = "dto.description")
    @Mapping(target = "burntCalories",    source = "dto.burntCalories")
    @Mapping(target = "durationInMinutes",source = "dto.durationInMinutes")
    @Mapping(target = "username",         source = "dto.username")
    @Mapping(target = "crtnDate",         ignore = true)
    @Mapping(target = "lastUpdDate",      ignore = true)
    Routine toEntity(RoutineDTO dto, Training training);

    // -----------------------------------------------------------------------
    // DTO → Entity  (with drills list — MM/dd/yyyy date format)
    // -----------------------------------------------------------------------

    /**
     * Creates a {@link Routine} with a pre-built drill list.
     *
     * <p>Date format: {@code MM/dd/yyyy} (legacy slash format).</p>
     */
    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "uuid",             expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId",            expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.refId())")
    @Mapping(target = "status",           expression = "java(com.abhi.leximentor.fitmate.constants.Status.RoutineStatus.NOT_STARTED)")
    @Mapping(target = "routineDate",      source = "dto.workoutDate", qualifiedByName = "routineDateFromSlashFormat")
    @Mapping(target = "training",         source = "training")
    @Mapping(target = "drills",           source = "drills")
    @Mapping(target = "description",      source = "dto.description")
    @Mapping(target = "burntCalories",    source = "dto.burntCalories")
    @Mapping(target = "durationInMinutes",source = "dto.durationInMinutes")
    @Mapping(target = "username",         source = "dto.username")
    @Mapping(target = "crtnDate",         ignore = true)
    @Mapping(target = "lastUpdDate",      ignore = true)
    Routine toEntityWithDrills(RoutineDTO dto, Training training, List<Drill> drills);
}
