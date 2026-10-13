package com.abhi.leximentor.inventory.service.inv.impl;

import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.inv.LanguageDTO;
import com.abhi.leximentor.inventory.entities.inv.Language;
import com.abhi.leximentor.inventory.repository.inv.LanguageRepository;
import com.abhi.leximentor.inventory.service.inv.LanguageService;
import com.abhi.leximentor.inventory.util.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepository languageRepository;

    @Override
    public LanguageDTO add(LanguageDTO dto) {
        log.info("Adding language. language={}", dto == null ? null : dto.getLanguage());
        Language language = Language.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).language(dto.getLanguage()).status(Status.ApplicationStatus.ACTIVE).build();
        language = languageRepository.save(language);
        LanguageDTO response = LanguageDTO.builder().refId(String.valueOf(language.getRefId())).status(Status.ApplicationStatus.getStatusStr(language.getStatus())).language(language.getLanguage()).build();
        log.info("Added language. refId={}", response.getRefId());
        return response;
    }

    @Override
    public LanguageDTO get(String language) {
        log.info("Fetching language. language={}", language);
        Language entityLang = languageRepository.findByLanguage(language);
        LanguageDTO response = LanguageDTO.builder().refId(String.valueOf(entityLang.getRefId())).status(Status.ApplicationStatus.getStatusStr(entityLang.getStatus())).language(entityLang.getLanguage()).build();
        log.info("Fetched language. language={}", language);
        return response;
    }
}
