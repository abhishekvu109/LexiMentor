package com.abhi.leximentor.inventory.service;

import com.abhi.leximentor.inventory.dto.WordDTO;

import java.util.Collection;

public interface WordService {
    public WordDTO add(WordDTO word);

    public Collection<WordDTO> addAll(Collection<WordDTO> words);

    public Collection<WordDTO> get(int limit);

    public Collection<WordDTO> getAll();

    public WordDTO get(long wordId);

    public Collection<WordDTO> get(String word);

    public Collection<WordDTO> getByPos(String pos);

    public Collection<WordDTO> getByCategory(String categoryId);

    public WordDTO update(WordDTO word);

    public boolean remove(WordDTO word);

    public boolean removeAll(Collection<WordDTO> words);
}
