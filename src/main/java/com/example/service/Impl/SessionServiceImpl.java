package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.po.Session;
import com.example.domain.po.User;
import com.example.exception.BadRequestException;
import com.example.mapper.SessionMapper;
import com.example.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session> implements SessionService {

    private final SessionMapper sessionMapper;

    @Override
    public List<Session> getAllSessionId(Long userId) {
        return sessionMapper.getAllSessionId(userId);
    }

    @Override
    public void createSession(Long userId) {
        Session session = Session.builder()
                .userId(userId)
                .title(String.valueOf(LocalDateTime.now()))
                .createTime(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        this.save(session);
    }

    @Override
    public void deleteSession(Long userId, Long id) {
        Session session = this.getById(id);
        if (!Objects.equals(session.getUserId(), userId)) {
            throw new BadRequestException("删除权限不足");
        }
        this.removeById(id);
    }

    @Override
    public void updateSession(Long userId, Long id, String title) {
        if (!Objects.equals(userId, this.getById(id).getUserId())) {
            throw new BadRequestException("修改权限不足");
        }
        this.update()
                .eq("id", id)
                .set("title", title)
                .set("update_date", LocalDateTime.now())
                .update();
    }
}
