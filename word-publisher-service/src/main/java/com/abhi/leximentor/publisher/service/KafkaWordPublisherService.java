package com.abhi.leximentor.publisher.service;

import com.abhi.leximentor.publisher.dto.WordDTO;

public interface KafkaWordPublisherService {
    public void post(WordDTO dto);
}
