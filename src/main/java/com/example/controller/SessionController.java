package com.example.controller;

import com.example.domain.ResponseResult;
import com.example.domain.entity.Session;
import com.example.service.SessionService;
import com.example.util.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ResponseResult<List<Session>> getAllSessionId() {
        Long userId = UserContextUtil.getUserId();
        return ResponseResult.success(sessionService.getAllSessionId(userId));
    }

    @PostMapping
    public ResponseResult<T> createSession() {
        Long userId = UserContextUtil.getUserId();
        sessionService.createSession(userId);
        return ResponseResult.success();
    }

    @DeleteMapping("/{id}")
    public ResponseResult<T> deleteSession(Long id) {
        Long userId = UserContextUtil.getUserId();
        sessionService.deleteSession(id, userId);
        return ResponseResult.success();
    }

    @PutMapping
    public ResponseResult<T> updateSession(@RequestParam Long id, @RequestParam String title) {
        Long userId = UserContextUtil.getUserId();
        sessionService.updateSession(id, userId, title);
        return ResponseResult.success();
    }
}
