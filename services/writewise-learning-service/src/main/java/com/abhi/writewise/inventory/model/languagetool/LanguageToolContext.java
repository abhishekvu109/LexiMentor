package com.abhi.writewise.inventory.model.languagetool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageToolContext {
    private String text;
    private int offset;
    private int length;
}
