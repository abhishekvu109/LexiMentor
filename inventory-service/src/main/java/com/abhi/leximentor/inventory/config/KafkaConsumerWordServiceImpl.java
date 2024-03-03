package com.abhi.leximentor.inventory.config;

import com.abhi.leximentor.inventory.constants.ApplicationConstants;
import com.abhi.leximentor.inventory.dto.WordDTO;
import com.abhi.leximentor.inventory.service.WordService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaConsumerWordServiceImpl {

    private final WordService wordService;

    @KafkaListener(topics = ApplicationConstants.KAFKA_TOPIC, groupId = ApplicationConstants.KAFKA_GROUP)
    public void consumeEvents(WordDTO wordDTO) {
        log.info("consumer consume the events {} ", wordDTO);
        WordDTO dto = wordService.add(wordDTO);
        log.info("The data has been written on the database: {}", dto);
    }
}
