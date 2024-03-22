package com.abhi.leximentor.inventory.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RestClient {
    private final RestTemplate restTemplate;

    public <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    }

    public <T> ResponseEntity<T> post(String url, HttpHeaders headers, T requestBody, Class<T> responseType) {
        HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
        log.info("The request before sending:{}", entity);
        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
    }

    public <T> ResponseEntity<T> put(String url, HttpHeaders headers, T requestBody, Class<T> responseType) {
        HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
    }

    public <T> ResponseEntity<Void> delete(String url, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }
}
