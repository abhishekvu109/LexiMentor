package com.abhi.leximentor.leximentor.service.inv.impl;

import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.inv.LanguageDTO;
import com.abhi.leximentor.leximentor.entities.inv.Language;
import com.abhi.leximentor.leximentor.repository.inv.LanguageRepository;
import com.abhi.leximentor.leximentor.service.inv.LanguageService;
import com.abhi.leximentor.leximentor.util.KeyGeneratorUtil;
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
