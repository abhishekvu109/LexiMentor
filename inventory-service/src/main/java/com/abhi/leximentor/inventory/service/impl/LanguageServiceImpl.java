package com.abhi.leximentor.inventory.service.impl;

import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.LanguageDTO;
import com.abhi.leximentor.inventory.entities.Language;
import com.abhi.leximentor.inventory.repository.LanguageRepository;
import com.abhi.leximentor.inventory.service.LanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepository languageRepository;

    @Override
    public LanguageDTO add(LanguageDTO dto) {
        Language language = Language.builder()
                .key(UUID.randomUUID().toString())
                .language(dto.getLanguage())
                .status(Status.ACTIVE)
                .build();
        language = languageRepository.save(language);
        return LanguageDTO.builder()
                .key(language.getKey())
                .status(Status.getStatus(language.getStatus()))
                .language(language.getLanguage())
                .build();
    }

    @Override
    public LanguageDTO get(String language) {
        Language entityLang = languageRepository.findByLanguage(language);
        return LanguageDTO.builder()
                .key(entityLang.getKey())
                .status(Status.getStatus(entityLang.getStatus()))
                .language(entityLang.getLanguage())
                .build();
    }
}
