package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.po.Session;
import com.example.domain.po.User;
import com.example.mapper.SessionMapper;
import com.example.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session> implements SessionService {

    private final SessionMapper sessionMapper;

    @Override
    public List<Session> getAllSessionId(Long userId) {
        return sessionMapper.getAllSessionId(userId);
    }
}
