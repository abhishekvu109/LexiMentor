package com.abhi.leximentor.inventory.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RestAdvancedUtil {

    private final RestTemplate restTemplate;

    public <T> T post(String URL, T object, Class<T> classType) {
        URI uri = null;
        try {
            uri = new URI(URL);
            log.info("REST URI is loaded");
        } catch (URISyntaxException ex) {
            log.error("Something is wrong with the URL: {}", ex.getMessage());
            throw new RuntimeException("Exception occurred while making a rest request");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<T> httpEntity = new HttpEntity<>(object, headers);
        ResponseEntity<T> result = restTemplate.postForEntity(uri, httpEntity, classType);
        return result.getBody();
    }
}
