package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domain.entity.Session;

import java.util.List;

public interface SessionService extends IService<Session> {

    List<Session> getAllSessionId(Long userId);

    void createSession(Long userId);

    void deleteSession(Long userId, Long id);

    void updateSession(Long userId, Long id, String title);
}
