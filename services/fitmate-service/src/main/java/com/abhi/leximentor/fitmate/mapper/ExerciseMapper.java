package com.abhi.leximentor.fitmate.mapper;

import com.abhi.leximentor.fitmate.dto.ExerciseDTO;
import com.abhi.leximentor.fitmate.dto.MuscleDTO;
import com.abhi.leximentor.fitmate.entities.BodyPart;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Muscle;
import com.abhi.leximentor.fitmate.entities.Training;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * MapStruct mapper for {@link Exercise} ↔ {@link ExerciseDTO}.
 *
 * <p>Replaces {@code FitmateServiceUtil.ExerciseUtil}.</p>
 *
 * <p><strong>Why an abstract class?</strong> The {@code targetMuscles} mapping
 * requires calling {@link MuscleMapper#toDto(Muscle, BodyPart)}, which is a
 * two-argument method.  MapStruct cannot select it automatically during a
 * list mapping, so we inject {@link MuscleMapper} as a Spring bean and delegate
 * from a protected helper method that MapStruct references via an
 * {@code expression}.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {FitmateMapperHelper.class, TrainingMapper.class, BodyPartMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ExerciseMapper {

    /** Injected by Spring into the generated {@code ExerciseMapperImpl}. */
    @Autowired
    protected MuscleMapper muscleMapper;

    // -----------------------------------------------------------------------
    // Entity → DTO
    // -----------------------------------------------------------------------

    /**
     * Maps an {@link Exercise} to an {@link ExerciseDTO}.
     *
     * <ul>
     *   <li>{@code bodyPart}     — mapped from {@code targetBodyPart} via {@link BodyPartMapper}.</li>
     *   <li>{@code targetMuscles}— mapped via {@link #mapMuscles(Exercise)}; each muscle
     *       DTO includes its parent body-part.</li>
     *   <li>{@code analyticsDTOList} — populated separately by the service; ignored here.</li>
     * </ul>
     */
    @Mapping(target = "refId",           source = "refId",           qualifiedByName = "longToString")
    @Mapping(target = "status",          source = "status",          qualifiedByName = "applicationStatusToString")
    @Mapping(target = "difficultyLevel", source = "difficultyLevel", qualifiedByName = "difficultyLevelToString")
    @Mapping(target = "riskLevel",       source = "riskLevel",       qualifiedByName = "riskLevelToString")
    @Mapping(target = "bodyPart",        source = "targetBodyPart")  // BodyPartMapper.toDto() selected automatically
    @Mapping(target = "training",        source = "training")        // TrainingMapper.toDto() selected automatically
    @Mapping(target = "targetMuscles",   expression = "java(mapMuscles(entity))")
    @Mapping(target = "analyticsDTOList",ignore = true)
    public abstract ExerciseDTO toDto(Exercise entity);

    // -----------------------------------------------------------------------
    // DTO → Entity  (primary creation path)
    // -----------------------------------------------------------------------

    /**
     * Creates a new {@link Exercise} entity.
     *
     * <ul>
     *   <li>{@code unit}         — normalised through {@link com.abhi.leximentor.fitmate.constants.Unit}.</li>
     *   <li>{@code status}       — defaults to ACTIVE when the DTO field is blank.</li>
     *   <li>{@code targetBodyPart} — supplied as the {@code bodyPart} parameter.</li>
     *   <li>{@code targetMuscles}  — supplied as the {@code muscles} parameter (may be null).</li>
     *   <li>{@code resources}      — not set on creation; managed separately.</li>
     * </ul>
     */
    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "uuid",            expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId",           expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.refId())")
    @Mapping(target = "name",            source = "dto.name")
    @Mapping(target = "description",     source = "dto.description")
    @Mapping(target = "equipments",      source = "dto.equipments")
    @Mapping(target = "unit",            source = "dto.unit",            qualifiedByName = "unitFromString")
    @Mapping(target = "status",          source = "dto.status",          qualifiedByName = "applicationStatusToInt")
    @Mapping(target = "difficultyLevel", source = "dto.difficultyLevel", qualifiedByName = "difficultyLevelToInt")
    @Mapping(target = "riskLevel",       source = "dto.riskLevel",       qualifiedByName = "riskLevelToInt")
    @Mapping(target = "training",        source = "training")
    @Mapping(target = "targetBodyPart",  source = "bodyPart")
    @Mapping(target = "targetMuscles",   source = "muscles")
    @Mapping(target = "resources",       ignore = true)
    @Mapping(target = "crtnDate",        ignore = true)
    @Mapping(target = "lastUpdDate",     ignore = true)
    public abstract Exercise toEntity(ExerciseDTO dto, Training training, BodyPart bodyPart, List<Muscle> muscles);

    // -----------------------------------------------------------------------
    // Internal helper
    // -----------------------------------------------------------------------

    /**
     * Maps the exercise's own {@code targetMuscles} list to DTOs, passing
     * {@code targetBodyPart} to {@link MuscleMapper} so each muscle DTO
     * carries its body-part details.
     */
    protected List<MuscleDTO> mapMuscles(Exercise entity) {
        if (CollectionUtils.isEmpty(entity.getTargetMuscles())) {
            return Collections.emptyList();
        }
        return entity.getTargetMuscles()
                .stream()
                .map(muscle -> muscleMapper.toDto(muscle, entity.getTargetBodyPart()))
                .toList();
    }
}
