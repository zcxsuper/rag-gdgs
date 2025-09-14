package com.example.controller;

import com.example.annotation.ChatFlow;
import com.example.domain.ResponseResult;
import com.example.exception.BadRequestException;
import com.example.service.Assistant;
import com.example.service.SessionService;
import com.example.util.UserContextUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantController {

    private final Assistant assistant;

    public AssistantController(Assistant assistant) {
        this.assistant = assistant;
    }

    // 流式展示
//    @GetMapping(value = "/chat",produces = "text/html;charset=utf-8")
//    public Flux<String> chat(String memoryId, String message) {
//        return assistant.chat(memoryId, message);
//    }

    /**
     * session -》sessionId:LOCAL/ONLINE
     *
     * @param session
     * @param message
     * @return
     */
    @ChatFlow
    @PostMapping("/chat")
    public ResponseResult<String> chat(@RequestParam String session, @RequestBody String message) {
        return ResponseResult.success(assistant.chat(session, message));
    }
}
