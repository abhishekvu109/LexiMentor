package com.abhi.leximentor.leximentor.service.inv;

import com.abhi.leximentor.leximentor.dto.inv.LanguageDTO;

public interface LanguageService {
    public LanguageDTO add(LanguageDTO dto);

    public LanguageDTO get(String language);
}
