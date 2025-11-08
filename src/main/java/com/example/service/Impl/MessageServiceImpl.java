package com.example.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.entity.Message;
import com.example.domain.entity.Session;
import com.example.exception.BadRequestException;
import com.example.mapper.MessageMapper;
import com.example.service.MessageService;
import com.example.service.SessionService;
import com.example.util.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final MessageMapper messageMapper;
    private final SessionService sessionService;


    @Override
    public List<Message> getMessageBySessionId(Long sessionId) {
        Long userId = UserContextUtil.getUserId();
        if(!userId.equals(sessionService.getById(sessionId).getUserId())){
            throw new BadRequestException("获取该会话记录权限不足");
        }
        return messageMapper.findBySessionId(sessionId);
    }

    @Override
    public PageDTO<Message> getMessageByPage(Long sessionId, Integer pageNum, Integer pageSize) {
        // 获取当前用户ID
        Long userId = UserContextUtil.getUserId();
        
        // 验证会话是否属于当前用户
        Session session = sessionService.getById(sessionId);
        if (session == null) {
            throw new BadRequestException("会话不存在");
        }
        if (!userId.equals(session.getUserId())) {
            throw new BadRequestException("获取该会话记录权限不足");
        }

        // 分页查询指定会话的消息，按创建时间倒序
        Page<Message> page = this.lambdaQuery()
                .eq(Message::getSessionId, sessionId) // 查询指定会话的消息
                .orderByDesc(Message::getCreated) // 按 created 倒序
                .page(new Page<>(pageNum, pageSize));

        // 获取分页记录
        List<Message> records = page.getRecords();

        // 如果没有记录，返回空的分页结果
        if (records.isEmpty()) {
            return new PageDTO<>(pageNum, pageSize, page.getTotal());
        }

        // 用 PageDTO 包装
        PageDTO<Message> result = new PageDTO<>(pageNum, pageSize, page.getTotal());
        result.setRecords(records);
        return result;
    }
}
