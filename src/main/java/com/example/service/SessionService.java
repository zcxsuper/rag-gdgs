package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domain.po.Session;
import com.example.domain.po.User;

import java.util.List;

public interface SessionService extends IService<Session> {

    List<Session> getAllSessionId(Long userId);
}
