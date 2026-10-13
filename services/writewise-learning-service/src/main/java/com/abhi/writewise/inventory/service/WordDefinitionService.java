package com.abhi.writewise.inventory.service;

import com.abhi.writewise.inventory.dto.WordDefinitionDTO;

public interface WordDefinitionService {
    WordDefinitionDTO generateWordDefinitionFromLlm(WordDefinitionDTO request);

}
