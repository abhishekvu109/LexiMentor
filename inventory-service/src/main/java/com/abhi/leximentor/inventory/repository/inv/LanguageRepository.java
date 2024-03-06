package com.abhi.leximentor.inventory.repository.inv;

import com.abhi.leximentor.inventory.entities.inv.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    public Language findByLanguage(String language);
}
