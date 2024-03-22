package com.abhi.leximentor.inventory.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RestUtil {
    private final ObjectMapper objectMapper;

    public <T> T post(String URL, Object requestBody, Class<T> responseType) {
        try {
            log.info("The request is :{}", requestBody);
            HttpClient httpClient = HttpClient.newHttpClient();
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URL);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(builder.toUriString())).POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody))).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            T responseObject = objectMapper.readValue(response.body(), responseType);
            log.info("Response received: {}", responseObject);
            return responseObject;
        } catch (Exception e) {
            log.error("An error occurred while making the POST request: {}", e.getMessage());
            throw new RuntimeException("An error occurred while making the POST request", e);
        }
    }

    public <T> T get(String URL, Class<T> responseType) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URL);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(builder.toUriString())).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            T responseObject = objectMapper.readValue(response.body(), responseType);
            log.info("Response received: {}", responseObject);
            return responseObject;
        } catch (Exception e) {
            log.error("An error occurred while making the GET request: {}", e.getMessage());
            throw new RuntimeException("An error occurred while making the GET request", e);
        }
    }
}
