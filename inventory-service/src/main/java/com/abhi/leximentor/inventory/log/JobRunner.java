package com.abhi.leximentor.inventory.log;

import com.abhi.leximentor.inventory.dto.*;
import com.abhi.leximentor.inventory.dto.other.DatamuseDTO;
import com.abhi.leximentor.inventory.dto.other.NltkDTO;
import com.abhi.leximentor.inventory.service.WordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Data
@Slf4j
@Builder
@AllArgsConstructor
public class JobRunner implements Runnable {
    private String nltk;
    private String datamuse;
    private String taskId;
    private String word;
    private WordService wordService;
    private ObjectMapper objectMapper;



    @Override
    public void run() {
        getResponse(word);
    }

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
                wordDTO = wordService.add(wordDTO);
                log.info("Word is saved in the database: {}", wordDTO);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }

        }).join(); // Wait for completion
    }

    public void buildWordDtoFromNltkResponse(WordDTO wordDTO, String word, NltkDTO nltkDTO) {
        wordDTO.setWord(word);
        wordDTO.setAntonyms(CollectionUtils.isEmpty(nltkDTO.getAntonyms()) ? null : nltkDTO.getAntonyms().stream().map(nltk -> AntonymDTO.builder().antonym(nltk).word(word).source("NLTK").build()).collect(Collectors.toList()));
        wordDTO.setSynonyms(CollectionUtils.isEmpty(nltkDTO.getSynonyms()) ? null : nltkDTO.getSynonyms().stream().map(nltk -> SynonymDTO.builder().word(word).synonym(nltk).source("NLTK").build()).collect(Collectors.toList()));
        wordDTO.setExamples(CollectionUtils.isEmpty(nltkDTO.getExamples()) ? null : nltkDTO.getExamples().stream().map(nltk -> ExampleDTO.builder().wordKey(word).example(nltk).source("NLTK").build()).collect(Collectors.toList()));
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
        if (!CollectionUtils.isEmpty(datamuseDTO.getAntonyms())) {
            Collection<AntonymDTO> antonymDTOS = wordDTO.getAntonyms();
            if (!CollectionUtils.isEmpty(antonymDTOS))
                antonymDTOS = new LinkedList<>();
            antonymDTOS = datamuseDTO.getAntonyms().stream().map(dm -> AntonymDTO.builder().antonym(dm).word(word).source("DATAMUSE").build()).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(datamuseDTO.getSynonyms())) {
            Collection<SynonymDTO> synonymDTOS = wordDTO.getSynonyms();
            if (!CollectionUtils.isEmpty(synonymDTOS))
                synonymDTOS = new LinkedList<>();
            synonymDTOS = datamuseDTO.getSynonyms().stream().map(dm -> SynonymDTO.builder().synonym(dm).word(word).source("DATAMUSE").build()).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(datamuseDTO.getExamples())) {
            Collection<ExampleDTO> exampleDTOS = wordDTO.getExamples();
            if (!CollectionUtils.isEmpty(exampleDTOS))
                exampleDTOS = new LinkedList<>();
            exampleDTOS = datamuseDTO.getExamples().stream().map(dm -> ExampleDTO.builder().example(dm).wordKey(word).source("DATAMUSE").build()).collect(Collectors.toList());
        }
        wordDTO.setPos(wordDTO.getPos() + "|" + datamuseDTO.getPos());
    }
}
