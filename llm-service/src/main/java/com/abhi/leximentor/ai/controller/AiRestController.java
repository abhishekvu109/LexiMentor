package com.abhi.leximentor.ai.controller;

import com.abhi.leximentor.ai.constants.ApplicationConstants;
import com.abhi.leximentor.ai.constants.UrlConstants;
import com.abhi.leximentor.ai.dto.PromptDTO;
import com.abhi.leximentor.ai.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AiRestController {
    private final ChatService chatService;

    @GetMapping(value = UrlConstants.GeneratePromptResponseUrl.GENERATE_PROMPT_URL, consumes = ApplicationConstants.MediaType.APPLICATION_JSON, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String generate(@RequestBody PromptDTO prompt) {
        return chatService.getPromptResult(prompt.getPrompt());
    }
}
