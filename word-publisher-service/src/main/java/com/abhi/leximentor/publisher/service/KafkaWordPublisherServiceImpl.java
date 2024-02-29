package com.abhi.leximentor.publisher.service;

import com.abhi.leximentor.publisher.constants.ApplicationConstants;
import com.abhi.leximentor.publisher.dto.WordDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaWordPublisherServiceImpl implements KafkaWordPublisherService {

    private final KafkaTemplate<String, WordDTO> kafkaTemplate;

    @Override
    public void post(WordDTO dto) {
        CompletableFuture<SendResult<String, WordDTO>> send = kafkaTemplate.send(ApplicationConstants.KAFKA_WORD_TOPIC_NAME, dto);
        send.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message is produced dto: {} on the offset: {}", dto, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send the dto: {} {}", dto, ex.getMessage());
            }
        });
    }
}
