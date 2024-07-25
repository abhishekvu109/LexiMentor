package com.abhi.leximentor.ai.controller;

import com.abhi.leximentor.ai.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AiRestController {
    private final ChatService chatService;

    @GetMapping("/api/v1/generate")
    public String generate(@RequestParam String prompt) {
        return chatService.getPromptResult(prompt);
    }
}
