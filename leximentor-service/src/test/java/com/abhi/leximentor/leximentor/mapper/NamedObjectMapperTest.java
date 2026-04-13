package com.abhi.leximentor.leximentor.mapper;

import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.NamedObjectDTO;
import com.abhi.leximentor.leximentor.entities.NamedObject;
import com.abhi.leximentor.leximentor.util.KeyGeneratorUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NamedObjectMapperTest {

    private final NamedObjectMapper mapper = new NamedObjectMapper();

    @Test
    void toEntity_shouldMapDtoToEntity() {
        NamedObjectDTO dto = NamedObjectDTO.builder()
                .name("name1")
                .genre("genre1")
                .subGenre("sub1")
                .description("desc1")
                .aliases(List.of("a1", "a2"))
                .tags(List.of("t1", "t2"))
                .build();

        NamedObject entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertNotNull(entity.getKey());
        assertEquals("name1", entity.getName());
        assertEquals("[\"a1\",\"a2\"]", entity.getAlias());
        assertEquals("[\"t1\",\"t2\"]", entity.getTags());
        assertEquals(Status.ApplicationStatus.ACTIVE, entity.getStatus());
    }

    @Test
    void toDto_shouldMapEntityToDto() {
        NamedObject entity = NamedObject.builder()
                .key(KeyGeneratorUtil.uuid())
                .name("name2")
                .genre("genre2")
                .subGenre("sub2")
                .description("desc2")
                .alias("[\"a1\",\"a2\"]")
                .tags("[\"t1\",\"t2\"]")
                .status(Status.ApplicationStatus.ACTIVE)
                .build();

        NamedObjectDTO dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertNotNull(dto.getKey());
        assertEquals("name2", dto.getName());
        assertEquals(List.of("\"a1\"", "\"a2\""), dto.getAliases());
        assertEquals(List.of("\"t1\"", "\"t2\""), dto.getTags());
        assertEquals("Active", dto.getStatus());
    }
}
