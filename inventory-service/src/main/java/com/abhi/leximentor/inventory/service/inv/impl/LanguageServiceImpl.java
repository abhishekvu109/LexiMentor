package com.abhi.leximentor.inventory.service.inv.impl;

import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.inv.LanguageDTO;
import com.abhi.leximentor.inventory.entities.inv.Language;
import com.abhi.leximentor.inventory.repository.inv.LanguageRepository;
import com.abhi.leximentor.inventory.service.inv.LanguageService;
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
        Language language = Language.builder().uuid(UUID.randomUUID().toString()).refId(UUID.randomUUID().getMostSignificantBits()).language(dto.getLanguage()).status(Status.ACTIVE).build();
        language = languageRepository.save(language);
        return LanguageDTO.builder().refId(language.getRefId()).status(Status.getStatus(language.getStatus())).language(language.getLanguage()).build();
    }

    @Override
    public LanguageDTO get(String language) {
        Language entityLang = languageRepository.findByLanguage(language);
        return LanguageDTO.builder().refId(entityLang.getRefId()).status(Status.getStatus(entityLang.getStatus())).language(entityLang.getLanguage()).build();
    }
}
