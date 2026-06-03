package com.abhi.writewise.inventory.model.languagetool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageToolMatch {
    private String message;
    private int offset;
    private int length;
    private LanguageToolContext context;
    private List<LanguageToolReplacement> replacements;
    private LanguageToolRule rule;
}
