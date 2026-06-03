package com.abhi.writewise.inventory.model.languagetool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageToolResponse {
    private List<LanguageToolMatch> matches;
}
