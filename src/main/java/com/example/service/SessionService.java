package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domain.entity.Session;

import java.util.List;

public interface SessionService extends IService<Session> {

    List<Session> getAllSessionId(Long userId);

    void createSession(Long userId, String title);

    void deleteSession(Long id, Long userId);

    void updateSession(Long id, Long userId, String title);
}
