package com.abhi.leximentor.inventory.service;

import com.abhi.leximentor.inventory.dto.LanguageDTO;

public interface LanguageService {
    public LanguageDTO add(LanguageDTO dto);

    public LanguageDTO get(String language);
}
