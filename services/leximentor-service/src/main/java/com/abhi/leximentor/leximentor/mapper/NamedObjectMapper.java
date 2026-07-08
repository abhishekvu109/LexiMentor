package com.abhi.leximentor.leximentor.mapper;

import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.NamedObjectDTO;
import com.abhi.leximentor.leximentor.entities.NamedObject;
import com.abhi.leximentor.leximentor.mapper.support.JsonListValueMapper;
import com.abhi.leximentor.leximentor.util.KeyGeneratorUtil;
import org.springframework.stereotype.Component;

@Component
public class NamedObjectMapper implements EntityMapper<NamedObjectDTO, NamedObject> {
    public static final NamedObjectMapper INSTANCE = new NamedObjectMapper();

    @Override
    public NamedObjectDTO toDto(NamedObject entity) {
        if (entity == null) {
            return null;
        }
        return NamedObjectDTO.builder()
                .key(entity.getKey())
                .name(entity.getName())
                .description(entity.getDescription())
                .aliases(JsonListValueMapper.fromStorage(entity.getAlias()))
                .tags(JsonListValueMapper.fromStorage(entity.getTags()))
                .status(Status.ApplicationStatus.getStatusStr(entity.getStatus()))
                .genre(entity.getGenre())
                .subGenre(entity.getSubGenre())
                .build();
    }

    @Override
    public NamedObject toEntity(NamedObjectDTO dto) {
        if (dto == null) {
            return null;
        }
        return NamedObject.builder()
                .key(KeyGeneratorUtil.uuid())
                .name(dto.getName())
                .alias(JsonListValueMapper.toStorage(dto.getAliases()))
                .tags(JsonListValueMapper.toStorage(dto.getTags()))
                .description(dto.getDescription())
                .genre(dto.getGenre())
                .status(Status.ApplicationStatus.ACTIVE)
                .subGenre(dto.getSubGenre())
                .build();
    }
}
