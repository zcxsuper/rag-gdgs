package com.example.controller;

import com.example.annotation.ChatFlow;
import com.example.domain.ResponseResult;
import com.example.exception.BadRequestException;
import com.example.service.Assistant;
import com.example.service.SessionService;
import com.example.util.UserContextUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assistant")
public class AssistantController {

    private final Assistant assistant;

    /**
     * session -》sessionId:LOCAL/ONLINE
     *
     * @param session
     * @param map {"message":...}
     * @return
     */
    /*@ChatFlow
    @PostMapping("/chat")
    public ResponseResult<String> chat(@RequestParam String session, @RequestBody Map<String,String> map) {
        String message = map.get("message");
        return ResponseResult.success(assistant.chat(session, message));
    }*/
    @ChatFlow
    @GetMapping(path = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<String> chat(@RequestParam String session, @RequestBody Map<String,String> map) {
        String message = map.get("message");
        return assistant.chat(session, message);
    }
}
