package com.abhi.leximentor.leximentor.repository.inv;

import com.abhi.leximentor.leximentor.entities.inv.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    Language findByLanguage(String language);
}
