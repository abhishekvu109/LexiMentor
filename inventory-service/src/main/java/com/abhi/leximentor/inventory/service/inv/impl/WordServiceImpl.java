package com.abhi.leximentor.inventory.service.inv.impl;

import com.abhi.leximentor.inventory.dto.inv.WordDTO;
import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import com.abhi.leximentor.inventory.repository.inv.WordMetadataRepository;
import com.abhi.leximentor.inventory.service.inv.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WordServiceImpl implements WordService {
    private final WordMetadataRepository wordRepository;
    private final ServiceImplUtil2 util;


    @Override
    @Transactional
    public WordDTO add(WordDTO word) {
        WordMetadata wordMetadata = util.new WordMetadataUtil().buildWordEntity(word);
        wordMetadata = wordRepository.save(wordMetadata);
        return util.new WordMetadataUtil().generateWordWrapper(wordMetadata);
    }

    @Override
    @Transactional
    public Collection<WordDTO> addAll(Collection<WordDTO> words) {
        Collection<WordMetadata> wordMetadataList = words.stream().map(word -> util.new WordMetadataUtil().buildWordEntity(word)).collect(Collectors.toList());
        wordMetadataList = wordRepository.saveAll(wordMetadataList);
        log.info("Data persisted. Total data: {}", wordMetadataList.size());
        return wordMetadataList.stream().map(entity -> util.new WordMetadataUtil().generateWordWrapper(entity)).collect(Collectors.toList());
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
        return null;
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
}
