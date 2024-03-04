package com.abhi.leximentor.publisher.service;

import com.abhi.leximentor.publisher.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RestClientServiceImpl {
    @Value("${nltk}")
    private String nltk;

    @Value("${datamuse}")
    private String datamuse;

    private final ObjectMapper objectMapper;
    private final KafkaWordPublisherService service;

    private CompletableFuture<NltkDTO> sendNlTkAsyncRequest(HttpClient httpClient, String url) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                NltkDTO nltkDTO = objectMapper.readValue(response.body(), NltkDTO.class);
                return nltkDTO;
            } catch (Exception e) {
                e.printStackTrace();
                return new NltkDTO(); // or handle the error as needed
            }
        });
    }

    private CompletableFuture<DatamuseDTO> sendDatamuseAsyncRequest(HttpClient httpClient, String url) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                DatamuseDTO datamuseDTO = objectMapper.readValue(response.body(), DatamuseDTO.class);
                return datamuseDTO;
            } catch (Exception e) {
                e.printStackTrace();
                return new DatamuseDTO(); // or handle the error as needed
            }
        });
    }


    public void getResponse(String word) {
        // Define the URLs

        // Create HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Create CompletableFuture for each URL
        CompletableFuture<NltkDTO> future1 = sendNlTkAsyncRequest(httpClient, nltk + word);
        CompletableFuture<DatamuseDTO> future2 = sendDatamuseAsyncRequest(httpClient, datamuse + word);

        // Combine the results when both requests are complete
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2);
        // Handle the results
        combinedFuture.thenAccept(ignored -> {
            NltkDTO nltkDTO = future1.join();
            DatamuseDTO datamuseDTO = future2.join();

            // Process responses as needed
//            System.out.println("Response from URL 1: " + nltkDTO);
//            System.out.println("Response from URL 2: " + datamuse);
            WordDTO wordDTO = WordDTO.builder().build();
            buildWordDtoFromNltkResponse(wordDTO, word, nltkDTO);
            buildWordDtoFromDatamuseResponse(wordDTO, word, datamuseDTO);
            try {
                service.post(wordDTO);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }

        }).join(); // Wait for completion
    }

    public void buildWordDtoFromNltkResponse(WordDTO wordDTO, String word, NltkDTO nltkDTO) {
        wordDTO.setWord(word);
        wordDTO.setAntonyms(CollectionUtils.isEmpty(nltkDTO.getAntonyms()) ? null : nltkDTO.getAntonyms().stream().map(nltk -> AntonymDTO.builder().antonym(nltk).word(word).source("NLTK").build()).collect(Collectors.toList()));
        wordDTO.setSynonyms(CollectionUtils.isEmpty(nltkDTO.getSynonyms()) ? null : nltkDTO.getSynonyms().stream().map(nltk -> SynonymDTO.builder().word(word).synonym(nltk).source("NLTK").build()).collect(Collectors.toList()));
        wordDTO.setExamples(CollectionUtils.isEmpty(nltkDTO.getExamples()) ? null : nltkDTO.getExamples().stream().map(nltk -> ExampleDTO.builder().word(word).example(nltk).source("NLTK").build()).collect(Collectors.toList()));
        if (nltkDTO.getDefinition() != null) {
            List<MeaningDTO> meaningDTOS = new LinkedList<>();
            meaningDTOS.add(MeaningDTO.builder().meaning(nltkDTO.getDefinition()).word(word).source("NLTK").build());
            wordDTO.setMeanings(meaningDTOS);
        }
        wordDTO.setLanguage("en-us");
        wordDTO.setPos(nltkDTO.getPos());
        wordDTO.setSource("NLTK");
    }

    public void buildWordDtoFromDatamuseResponse(WordDTO wordDTO, String word, DatamuseDTO datamuseDTO) {
        if (wordDTO == null)
            wordDTO = WordDTO.builder().build();
        if (CollectionUtils.isNotEmpty(datamuseDTO.getAntonyms())) {
            Collection<AntonymDTO> antonymDTOS = wordDTO.getAntonyms();
            if (CollectionUtils.isNotEmpty(antonymDTOS))
                antonymDTOS = new LinkedList<>();
            antonymDTOS = datamuseDTO.getAntonyms().stream().map(dm -> AntonymDTO.builder().antonym(dm).word(word).source("DATAMUSE").build()).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(datamuseDTO.getSynonyms())) {
            Collection<SynonymDTO> synonymDTOS = wordDTO.getSynonyms();
            if (CollectionUtils.isNotEmpty(synonymDTOS))
                synonymDTOS = new LinkedList<>();
            synonymDTOS = datamuseDTO.getSynonyms().stream().map(dm -> SynonymDTO.builder().synonym(dm).word(word).source("DATAMUSE").build()).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(datamuseDTO.getExamples())) {
            Collection<ExampleDTO> exampleDTOS = wordDTO.getExamples();
            if (CollectionUtils.isNotEmpty(exampleDTOS))
                exampleDTOS = new LinkedList<>();
            exampleDTOS = datamuseDTO.getExamples().stream().map(dm -> ExampleDTO.builder().example(dm).word(word).source("DATAMUSE").build()).collect(Collectors.toList());
        }
        wordDTO.setPos(wordDTO.getPos() + "|" + datamuseDTO.getPos());
    }
}
