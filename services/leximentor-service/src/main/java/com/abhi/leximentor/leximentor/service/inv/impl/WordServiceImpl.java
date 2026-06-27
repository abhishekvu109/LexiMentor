package com.abhi.leximentor.leximentor.service.inv.impl;

import com.abhi.leximentor.leximentor.dto.inv.*;
import com.abhi.leximentor.leximentor.entities.inv.*;
import com.abhi.leximentor.leximentor.exceptions.entities.ServerException;
import com.abhi.leximentor.leximentor.mapper.InventoryDomainMapper;
import com.abhi.leximentor.leximentor.repository.inv.LanguageRepository;
import com.abhi.leximentor.leximentor.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.leximentor.service.inv.WordService;
import com.abhi.leximentor.leximentor.util.CollectionUtil;
import com.abhi.leximentor.leximentor.util.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordServiceImpl implements WordService {
    private final InventoryDomainMapper inventoryDomainMapper;
    private final LanguageRepository languageRepository;
    private final WordMetadataRepository wordRepository;
    private final CollectionUtil collectionUtil;
    private static final int LLM_RETRY_COUNT = 3;
    private final RestClient restClient;
    private String URL;
    private final static String WRITEWISE_LLM = "writewise-llm-service";


    @Override
    @Transactional
    public WordDTO add(WordDTO word) {
        log.info("Adding word. word={}", word == null ? null : word.getWord());
        WordMetadata wordMetadata = inventoryDomainMapper.toEntity(word);
        wordMetadata = wordRepository.save(wordMetadata);
        WordDTO response = inventoryDomainMapper.toDto(wordMetadata);
        log.info("Added word. key={}", response.getKey());
        return response;
    }

    @Override
    @Transactional
    public Collection<WordDTO> addAll(Collection<WordDTO> words) {
        log.info("Adding words. count={}", words == null ? 0 : words.size());
        Collection<WordMetadata> wordMetadataList = words.stream().map(inventoryDomainMapper::toEntity).collect(Collectors.toList());
        wordMetadataList = wordRepository.saveAll(wordMetadataList);
        log.info("Data persisted. Total data: {}", wordMetadataList.size());
        Collection<WordDTO> response = wordMetadataList.stream().map(inventoryDomainMapper::toDto).collect(Collectors.toList());
        log.info("Added words. count={}", response.size());
        return response;
    }

    @Override
    public Collection<WordDTO> get(int limit) {
        return null;
    }

    @Override
    public Collection<WordDTO> getAll() {
        return null;
    }

    @Override
    public WordDTO getByKey(String wordKey) {
        log.info("Fetching word by key={}", wordKey);
        WordMetadata wordMetadata = wordRepository.findByKey(wordKey).orElseThrow(
                () -> new ServerException().new EntityObjectNotFound("Word metadata not found for key: " + wordKey)
        );
        WordDTO response = inventoryDomainMapper.toDto(wordMetadata);
        log.info("Fetched word by key={}", wordKey);
        return response;
    }

    @Override
    public Collection<WordDTO> get(String word) {
        return null;
    }

    @Override
    public Collection<WordDTO> getByPos(String pos) {
        return null;
    }

    @Override
    public Collection<WordDTO> getByCategory(String categoryId) {
        return null;
    }

    @Override
    public WordDTO update(WordDTO word) {
        return null;
    }

    @Override
    public boolean remove(WordDTO word) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<WordDTO> words) {
        return false;
    }

    @Override
    public Set<String> getUniqueSourcesByWordKey(String wordKey) {
        log.info("Fetching unique sources. wordKey={}", wordKey);
        WordMetadata wordMetadata = wordRepository.findByKey(wordKey).orElseThrow(
                () -> new ServerException().new EntityObjectNotFound("Word metadata not found for key: " + wordKey)
        );
        Set<String> sources = new HashSet<>();
        sources.add(wordMetadata.getSource());
        Set<String> synSources = wordMetadata.getSynonyms().stream().map(Synonym::getSource).collect(Collectors.toSet());
        Set<String> antSources = wordMetadata.getAntonyms().stream().map(Antonym::getSource).collect(Collectors.toSet());
        Set<String> meanSources = wordMetadata.getMeanings().stream().map(Meaning::getSource).collect(Collectors.toSet());
        Set<String> posSources = wordMetadata.getPartsOfSpeeches().stream().map(PartsOfSpeech::getSource).collect(Collectors.toSet());
        Set<String> exampleSources = wordMetadata.getExamples().stream().map(Example::getSource).collect(Collectors.toSet());
        sources.addAll(synSources);
        sources.addAll(antSources);
        sources.addAll(meanSources);
        sources.addAll(posSources);
        sources.addAll(exampleSources);
        log.info("Fetched unique sources. wordKey={}, count={}", wordKey, sources.size());
        return sources;
    }

    @Override
    public WordDTO getWordByWordKeyAndSource(String source, String wordKey) {
        log.info("Fetching word by key and source. wordKey={}, source={}", wordKey, source);
        WordMetadata wordMetadata = wordRepository.findByKey(wordKey).orElseThrow(
                () -> new ServerException().new EntityObjectNotFound("Word metadata not found for key: " + wordKey)
        );
        WordDTO wordDTO = inventoryDomainMapper.toDto(wordMetadata);
        List<SynonymDTO> synonyms = wordMetadata.getSynonyms().stream().filter(syn -> syn.getSource().equalsIgnoreCase(source)).toList().stream().map(inventoryDomainMapper::toDto).toList();
        List<AntonymDTO> antonyms = wordMetadata.getAntonyms().stream().filter(ant -> ant.getSource().equalsIgnoreCase(source)).toList().stream().map(inventoryDomainMapper::toDto).toList();
        List<MeaningDTO> meanings = wordMetadata.getMeanings().stream().filter(mean -> mean.getSource().equalsIgnoreCase(source)).toList().stream().map(inventoryDomainMapper::toDto).toList();
        List<PartsOfSpeechDTO> posS = wordMetadata.getPartsOfSpeeches().stream().filter(pos -> pos.getSource().equalsIgnoreCase(source)).toList().stream().map(inventoryDomainMapper::toDto).toList();
        List<ExampleDTO> examples = wordMetadata.getExamples().stream().filter(ex -> ex.getSource().equalsIgnoreCase(source)).toList().stream().map(inventoryDomainMapper::toDto).toList();
        wordDTO.setSynonyms(synonyms);
        wordDTO.setAntonyms(antonyms);
        wordDTO.setMeanings(meanings);
        wordDTO.setPartsOfSpeeches(posS);
        wordDTO.setExamples(examples);
        log.info("Fetched word by key and source. wordKey={}, source={}", wordKey, source);
        return wordDTO;
    }

    @Override
    public WordDTO generateWordMetadataFromLLM(String word) {
        log.info("Generating word metadata from LLM. word={}", word);
        loadModelServiceName();
        log.info("Found the URl of the writewise service: {}", URL);
        LinkedList<String> requestWords = new LinkedList<>();
        requestWords.add(word);
        GenerateWordMetadataLlmDTO request = GenerateWordMetadataLlmDTO.builder().words(requestWords).prompt("").response("").build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<GenerateWordMetadataLlmDTO> responseEntity = null;
        GenerateWordMetadataLlmDTO responseOutput = null;
        int retry = LLM_RETRY_COUNT;
        while (retry > 0) {
            try {
                responseEntity = restClient.post(this.URL, headers, request, GenerateWordMetadataLlmDTO.class);
                responseOutput = responseEntity.getBody();
                log.info("The llm service has returned a response : {}", responseEntity);
                break;
            } catch (Exception ex) {
                log.error("Unable to get response from the llm service {} for {}", WRITEWISE_LLM, request);
                log.error(ex.getMessage());
                log.info("Attempting retry : {}", (LLM_RETRY_COUNT - retry));
                retry--;
            }
        }
        if (responseOutput == null)
            throw new ServerException().new InternalError("Writewise service has returned NULL response.");
        if (StringUtils.isNotEmpty(responseOutput.getResponse())) {
            String jsonResponse = this.extractJsonFromResponse(responseOutput.getResponse());
            log.info("Extracted the JSON inside the <response> marker : {}", jsonResponse);
            WordDTO dto = generateObjectFromTheJson(jsonResponse);
            log.info("Generated word metadata from LLM. word={}", word);
            return dto;
        } else {
            log.error("The response is null : {}", responseOutput.getResponse());
            return null;
        }
    }

    private void loadModelServiceName() {
        YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        yamlFactory.setResources(new ClassPathResource("application.yaml"));
        Properties properties = yamlFactory.getObject();
        if (properties == null) {
            log.error("Unable to load configuration from application.yaml");
            return;
        }
        log.info("Successfully found the llm topic address: {}", properties.getProperty(WRITEWISE_LLM));
        setURL(properties.getProperty(WRITEWISE_LLM));
    }

    private String extractJsonFromResponse(String response) {
        Pattern pattern = Pattern.compile("<response>(.*?)</response>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        throw new IllegalArgumentException("No valid JSON found in the response");
    }

    private WordDTO generateObjectFromTheJson(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response, WordDTO.class);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }
}
