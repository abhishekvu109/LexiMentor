package com.abhi.leximentor.inventory.service.inv.impl;

import com.abhi.leximentor.inventory.dto.inv.*;
import com.abhi.leximentor.inventory.entities.inv.*;
import com.abhi.leximentor.inventory.repository.inv.LanguageRepository;
import com.abhi.leximentor.inventory.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.inventory.service.inv.WordService;
import com.abhi.leximentor.inventory.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordServiceImpl implements WordService {
    private final InventoryServiceUtil util;
    private final LanguageRepository languageRepository;
    private final WordMetadataRepository wordRepository;
    private final CollectionUtil collectionUtil;

    @Override
    @Transactional
    public WordDTO add(WordDTO word) {
        WordMetadata wordMetadata = InventoryServiceUtil.WordMetadataUtil.buildEntity(word, wordRepository, languageRepository);
        wordMetadata = wordRepository.save(wordMetadata);
        return InventoryServiceUtil.WordMetadataUtil.buildDTO(wordMetadata);
    }

    @Override
    @Transactional
    public Collection<WordDTO> addAll(Collection<WordDTO> words) {
        Collection<WordMetadata> wordMetadataList = words.stream().map(d -> InventoryServiceUtil.WordMetadataUtil.buildEntity(d, wordRepository, languageRepository)).collect(Collectors.toList());
        wordMetadataList = wordRepository.saveAll(wordMetadataList);
        log.info("Data persisted. Total data: {}", wordMetadataList.size());
        return wordMetadataList.stream().map(InventoryServiceUtil.WordMetadataUtil::buildDTO).collect(Collectors.toList());
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
    public WordDTO get(long wordId) {
        WordMetadata wordMetadata = wordRepository.findByRefId(wordId);
        return InventoryServiceUtil.WordMetadataUtil.buildDTO(wordMetadata);
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
    public Set<String> getUniqueSourcesByWordRefId(long wordRefId) {
        WordMetadata wordMetadata = wordRepository.findByRefId(wordRefId);
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
        return sources;
    }

    @Override
    public WordDTO getWordByWordRefIdAndSource(String source, long wordRefId) {
        WordMetadata wordMetadata = wordRepository.findByRefId(wordRefId);
        WordDTO wordDTO = InventoryServiceUtil.WordMetadataUtil.buildDTO(wordMetadata);
        List<SynonymDTO> synonyms = wordMetadata.getSynonyms().stream().filter(syn -> syn.getSource().equals(source)).toList().stream().map(InventoryServiceUtil.SynonymUtil::buildDTO).toList();
        List<AntonymDTO> antonyms = wordMetadata.getAntonyms().stream().filter(ant -> ant.getSource().equals(source)).toList().stream().map(InventoryServiceUtil.AntonymUtil::buildDTO).toList();
        List<MeaningDTO> meanings = wordMetadata.getMeanings().stream().filter(mean -> mean.getSource().equals(source)).toList().stream().map(InventoryServiceUtil.MeaningUtil::buildDTO).toList();
        List<PartsOfSpeechDTO> posS = wordMetadata.getPartsOfSpeeches().stream().filter(pos -> pos.getSource().equals(source)).toList().stream().map(InventoryServiceUtil.PartsOfSpeechUtil::buildDTO).toList();
        List<ExampleDTO> examples = wordMetadata.getExamples().stream().filter(ex -> ex.getSource().equals(source)).toList().stream().map(InventoryServiceUtil.ExampleUtil::buildDTO).toList();
        wordDTO.setSynonyms(synonyms);
        wordDTO.setAntonyms(antonyms);
        wordDTO.setMeanings(meanings);
        wordDTO.setPartsOfSpeeches(posS);
        wordDTO.setExamples(examples);
        return wordDTO;
    }
}
