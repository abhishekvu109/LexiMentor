package com.abhi.leximentor.inventory.service.inv;

import com.abhi.leximentor.inventory.dto.inv.LanguageDTO;

public interface LanguageService {
    public LanguageDTO add(LanguageDTO dto);

    public LanguageDTO get(String language);
}
