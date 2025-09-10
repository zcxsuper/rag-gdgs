package com.example.controller;

import com.example.domain.ResponseResult;
import com.example.domain.po.Session;
import com.example.service.SessionService;
import com.example.util.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/session")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping()
    public ResponseResult<List<Session>> getAllSessionId() {
        Long userId = UserContextUtil.getUserId();
        return ResponseResult.success(sessionService.getAllSessionId(userId));
    }

}
