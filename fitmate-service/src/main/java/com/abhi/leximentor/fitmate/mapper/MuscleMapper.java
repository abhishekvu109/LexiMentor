package com.abhi.leximentor.fitmate.mapper;

import com.abhi.leximentor.fitmate.constants.Status;
import com.abhi.leximentor.fitmate.dto.BodyPartsDTO;
import com.abhi.leximentor.fitmate.dto.MuscleDTO;
import com.abhi.leximentor.fitmate.entities.BodyPart;
import com.abhi.leximentor.fitmate.entities.Muscle;
import org.mapstruct.*;

/**
 * MapStruct mapper for {@link Muscle} ↔ {@link MuscleDTO}.
 *
 * <p>Replaces {@code FitmateServiceUtil.MuscleUtil}.</p>
 *
 * <p><strong>Design note:</strong> {@code Muscle.bodyPart} stores a {@code Long} refId,
 * NOT a {@link BodyPart} object. The caller must therefore supply the resolved
 * {@link BodyPart} entity when mapping to a DTO.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {FitmateMapperHelper.class, BodyPartMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MuscleMapper {

    // -----------------------------------------------------------------------
    // Entity → DTO  (full BodyPart object available)
    // -----------------------------------------------------------------------

    /**
     * Maps a {@link Muscle} to a DTO, using the explicitly supplied
     * {@link BodyPart} to populate {@code bodyPart}.
     *
     * @param muscle    the muscle entity
     * @param bodyPart  the resolved body-part entity (matches muscle.bodyPart refId)
     */
    @Mapping(target = "refId",    source = "muscle.refId",  qualifiedByName = "longToString")
    @Mapping(target = "status",   source = "muscle.status", qualifiedByName = "applicationStatusToString")
    @Mapping(target = "name",     source = "muscle.name")
    @Mapping(target = "description", source = "muscle.description")
    @Mapping(target = "bodyPart", source = "bodyPart")   // BodyPartMapper.toDto() is auto-selected
    MuscleDTO toDto(Muscle muscle, BodyPart bodyPart);

    // -----------------------------------------------------------------------
    // Entity → DTO  (only the body-part name is known)
    // -----------------------------------------------------------------------

    /**
     * Lightweight variant used when only the body-part <em>name</em> is available
     * (e.g. inside export / analytics paths).
     */
    default MuscleDTO toDto(Muscle muscle, String bodyPartName) {
        if (muscle == null) return null;
        return MuscleDTO.builder()
                .refId(String.valueOf(muscle.getRefId()))
                .name(muscle.getName())
                .description(muscle.getDescription())
                .status(Status.ApplicationStatus.getStatusStr(muscle.getStatus()))
                .bodyPart(BodyPartsDTO.builder().name(bodyPartName).build())
                .build();
    }

    // -----------------------------------------------------------------------
    // DTO → Entity
    // -----------------------------------------------------------------------

    /**
     * Creates a new {@link Muscle} entity from the DTO and the resolved
     * {@link BodyPart}.
     *
     * <ul>
     *   <li>{@code bodyPart} (Long) is set from {@code bodyPart.refId}.</li>
     *   <li>{@code id}, {@code uuid}, {@code refId} are generated / JPA-managed.</li>
     *   <li>{@code resources} relationship is ignored on creation.</li>
     * </ul>
     */
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "uuid",      expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId",     expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.refId())")
    @Mapping(target = "name",      source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "bodyPart",  source = "bodyPart.refId")   // Long refId stored in Muscle
    @Mapping(target = "status",    source = "dto.status", qualifiedByName = "applicationStatusToInt")
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "crtnDate",  ignore = true)
    @Mapping(target = "lastUpdDate", ignore = true)
    Muscle toEntity(MuscleDTO dto, BodyPart bodyPart);
}
