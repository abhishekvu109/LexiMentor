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
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
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
    private Long wordId;
    private WordService wordService;
    private ObjectMapper objectMapper;
    private LoadLoggingService loadLoggingService;

    @Override
    public void run() {
        log.info("Worker name :{}  word:{}", Thread.currentThread().getName(), word);
        getResponse(word);
    }

    private CompletableFuture<NltkDTO> sendNlTkAsyncRequest(HttpClient httpClient, String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                        .queryParam("word", word);
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(builder.toUriString())).build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                NltkDTO nltkDTO = objectMapper.readValue(response.body(), NltkDTO.class);
                log.info("NLTK service has returned response for {} - response : {}", word, nltkDTO);
                return nltkDTO;
            } catch (Exception e) {
                log.error("An error has occurred while fetching from NLTK {} exception {}", word, e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    private CompletableFuture<DatamuseDTO> sendDatamuseAsyncRequest(HttpClient httpClient, String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                        .queryParam("word", word);
                log.info("URI:{}", builder.toUriString());
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(builder.toUriString())).build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                DatamuseDTO datamuseDTO = objectMapper.readValue(response.body(), DatamuseDTO.class);
                log.info("Datamuse service has returned response for {} - response : {}", word, datamuseDTO);
                return datamuseDTO;
            } catch (Exception e) {
                log.error("An error has occurred while fetching from datamuse {} exception {}", word, e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        });
    }


    public void getResponse(String word) {
        HttpClient httpClient = HttpClient.newHttpClient();
        CompletableFuture<NltkDTO> future1 = sendNlTkAsyncRequest(httpClient, nltk);
        CompletableFuture<DatamuseDTO> future2 = sendDatamuseAsyncRequest(httpClient, datamuse);
//        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2);
        CompletableFuture<Object> combinedFuture = CompletableFuture.anyOf(future1, future2);
        combinedFuture.thenAccept(ignored -> {
            NltkDTO nltkDTO = future1.join();
            DatamuseDTO datamuseDTO = future2.join();
            WordDTO wordDTO = WordDTO.builder().build();
            buildWordDtoFromNltkResponse(wordDTO, word, nltkDTO);
            buildWordDtoFromDatamuseResponse(wordDTO, word, datamuseDTO);
            try {
                wordDTO = wordService.add(wordDTO);
                log.info("Word is saved in the database: {}", wordDTO);
                loadLoggingService.updateStatus(wordId, 1);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }).join();
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
        if (nltkDTO.getPos() != null) {
            List<PartsOfSpeechDTO> partsOfSpeechDTOS = new LinkedList<>();
            partsOfSpeechDTOS.add(PartsOfSpeechDTO.builder().pos(nltkDTO.getPos()).word(word).source("NLTK").build());
            wordDTO.setPartsOfSpeeches(partsOfSpeechDTOS);
        }
        wordDTO.setLanguage("en-us");
        wordDTO.setPos(nltkDTO.getPos());
        wordDTO.setSource("NLTK");
    }

    public void buildWordDtoFromDatamuseResponse(WordDTO wordDTO, String word, DatamuseDTO datamuseDTO) {
        if (wordDTO == null) wordDTO = WordDTO.builder().build();
        if (!CollectionUtils.isEmpty(datamuseDTO.getAntonyms())) {
            Collection<AntonymDTO> antonymDTOS = wordDTO.getAntonyms();
            if (!CollectionUtils.isEmpty(antonymDTOS)) antonymDTOS = new LinkedList<>();
            antonymDTOS = datamuseDTO.getAntonyms().stream().map(dm -> AntonymDTO.builder().antonym(dm).word(word).source("DATAMUSE").build()).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(datamuseDTO.getSynonyms())) {
            Collection<SynonymDTO> synonymDTOS = wordDTO.getSynonyms();
            if (!CollectionUtils.isEmpty(synonymDTOS)) synonymDTOS = new LinkedList<>();
            synonymDTOS = datamuseDTO.getSynonyms().stream().map(dm -> SynonymDTO.builder().synonym(dm).word(word).source("DATAMUSE").build()).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(datamuseDTO.getExamples())) {
            Collection<ExampleDTO> exampleDTOS = wordDTO.getExamples();
            if (!CollectionUtils.isEmpty(exampleDTOS)) exampleDTOS = new LinkedList<>();
            exampleDTOS = datamuseDTO.getExamples().stream().map(dm -> ExampleDTO.builder().example(dm).wordKey(word).source("DATAMUSE").build()).collect(Collectors.toList());
        }
        if (datamuseDTO.getPos() != null) {
            Collection<PartsOfSpeechDTO> partsOfSpeechDTOS = wordDTO.getPartsOfSpeeches();
            if (partsOfSpeechDTOS == null || partsOfSpeechDTOS.isEmpty())
                partsOfSpeechDTOS = new LinkedList<>();
            partsOfSpeechDTOS.add(PartsOfSpeechDTO.builder().pos(datamuseDTO.getPos()).word(word).source("DATAMUSE").build());
            wordDTO.setPartsOfSpeeches(partsOfSpeechDTOS);
        }
        wordDTO.setPos(wordDTO.getPos() + "|" + datamuseDTO.getPos());
    }
}
