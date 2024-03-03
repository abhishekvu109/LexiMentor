package com.abhi.leximentor.inventory.kafka;

import com.abhi.leximentor.inventory.dto.WordDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
@NoArgsConstructor
public class WordDTODeserializer implements Deserializer<WordDTO> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WordDTO deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, WordDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
