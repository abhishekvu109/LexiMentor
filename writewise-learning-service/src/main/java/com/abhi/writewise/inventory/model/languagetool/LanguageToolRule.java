package com.abhi.writewise.inventory.model.languagetool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageToolRule {
    private String id;
    private String issueType;
    private LanguageToolCategory category;
}
