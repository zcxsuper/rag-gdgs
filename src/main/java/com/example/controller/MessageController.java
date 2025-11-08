package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.example.domain.ResponseResult;
import com.example.domain.entity.Message;
import com.example.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/session/{id}")
    public ResponseResult<List<Message>> getAllMessageBySessionId(@PathVariable("id") Long sessionId) {
        return ResponseResult.success(messageService.getMessageBySessionId(sessionId));
    }

    @GetMapping("/session/{id}/page")
    public ResponseResult<PageDTO<Message>> getMessageByPage(@PathVariable("id") Long sessionId,
                                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        //分页查询对话消息
        PageDTO<Message> page = messageService.getMessageByPage(sessionId, pageNum, pageSize);
        return ResponseResult.success(page);
    }
}
