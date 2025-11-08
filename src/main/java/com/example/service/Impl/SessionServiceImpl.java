package com.example.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.entity.Session;
import com.example.exception.BadRequestException;
import com.example.mapper.SessionMapper;
import com.example.service.SessionService;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.stereotype.Service;

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
    public void createSession(Long userId, String title) {
        Session session = Session.builder()
                .userId(userId)
                .title(title)
                .build();
        this.save(session);
    }

    @Override
    public void deleteSession(Long id, Long userId) {
        Session session = this.getById(id);
        if (Objects.isNull(session)) {
            throw new DatabaseException("会话为空, ID: " + id);
        }
        if (!Objects.equals(session.getUserId(), userId)) {
            throw new BadRequestException("删除权限不足");
        }
        this.removeById(id);
    }

    @Override
    public void updateSession(Long id, Long userId, String title) {
        if (!Objects.equals(userId, this.getById(id).getUserId())) {
            throw new BadRequestException("修改权限不足");
        }
        this.update()
                .eq("id", id)
                .set("title", title)
                .update();
    }
}
