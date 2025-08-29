package com.example.controller;

import com.example.service.Assistant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final Assistant assistant;

    public ChatController(Assistant assistant) {
        this.assistant = assistant;
    }

    @GetMapping(value = "/chat",produces = "text/html;charset=utf-8")
    public Flux<String> chat(String memoryId, String message) {
        return assistant.chat(memoryId, message);
    }
}
