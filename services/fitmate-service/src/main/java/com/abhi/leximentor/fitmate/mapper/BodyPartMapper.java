package com.abhi.leximentor.fitmate.mapper;

import com.abhi.leximentor.fitmate.dto.BodyPartsDTO;
import com.abhi.leximentor.fitmate.entities.BodyPart;
import org.mapstruct.*;

/**
 * MapStruct mapper for {@link BodyPart} ↔ {@link BodyPartsDTO}.
 *
 * <p>Replaces {@code FitmateServiceUtil.BodyPartsUtil}.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {FitmateMapperHelper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BodyPartMapper {

    // -----------------------------------------------------------------------
    // Entity → DTO
    // -----------------------------------------------------------------------

    @Mapping(target = "refId",  source = "refId",  qualifiedByName = "longToString")
    @Mapping(target = "status", source = "status",  qualifiedByName = "applicationStatusToString")
    BodyPartsDTO toDto(BodyPart entity);

    // -----------------------------------------------------------------------
    // DTO → Entity
    // -----------------------------------------------------------------------

    /**
     * Creates a new {@link BodyPart} entity from a DTO.
     *
     * <ul>
     *   <li>{@code id}             — managed by JPA; ignored.</li>
     *   <li>{@code uuid}           — generated.</li>
     *   <li>{@code refId}          — generated.</li>
     *   <li>{@code exercises}      — relationship; ignored on creation.</li>
     *   <li>{@code targetMuscles}  — relationship; ignored on creation.</li>
     *   <li>{@code resources}      — relationship; ignored on creation.</li>
     * </ul>
     */
    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "uuid",          expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.uuid())")
    @Mapping(target = "refId",         expression = "java(com.abhi.leximentor.fitmate.util.KeyGeneratorUtil.refId())")
    @Mapping(target = "status",        source = "status", qualifiedByName = "applicationStatusToInt")
    @Mapping(target = "exercises",     ignore = true)
    @Mapping(target = "targetMuscles", ignore = true)
    @Mapping(target = "resources",     ignore = true)
    BodyPart toEntity(BodyPartsDTO dto);
}
