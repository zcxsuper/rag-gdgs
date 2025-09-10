package com.example.controller;

import com.example.domain.ResponseResult;
import com.example.domain.po.Message;
import com.example.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/message")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/session/{id}")
    public ResponseResult<List<Message>> getAllMessageBySessionId(@PathVariable("id") Long sessionId) {
        return ResponseResult.success(messageService.getMessageBySessionId(sessionId));
    }
}
