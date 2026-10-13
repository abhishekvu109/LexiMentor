package com.abhi.leximentor.leximentor.service.base;

import com.abhi.leximentor.leximentor.exceptions.entities.InvalidDTOException;
import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractApplicationServiceTest {

    private final TestService service = new TestService();

    @Test
    void parseRefId_shouldParseValidValue() {
        assertEquals(42L, service.parse("42"));
    }

    @Test
    void parseRefId_shouldThrowForInvalidValue() {
        assertThrows(InvalidDTOException.class, () -> service.parse("abc"));
    }

    @Test
    void requireEntity_shouldThrowWhenNull() {
        assertThrows(ServerException.EntityObjectNotFound.class, () -> service.requireNullEntity());
    }

    private static class TestService extends AbstractApplicationService {
        long parse(String value) {
            return parseRefId(value, "refId");
        }

        void requireNullEntity() {
            requireEntity(null, "missing");
        }
    }
}
