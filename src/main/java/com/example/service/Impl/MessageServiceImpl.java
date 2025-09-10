package com.example.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.po.Message;
import com.example.domain.po.Session;
import com.example.exception.BadRequestException;
import com.example.mapper.MessageMapper;
import com.example.service.MessageService;
import com.example.service.SessionService;
import com.example.util.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final MessageMapper messageMapper;
    private final SessionService sessionService;


    @Override
    public List<Message> getMessageBySessionId(Long sessionId) {
//        Long userId = UserContextUtil.getUserId();
//        if(!userId.equals(sessionService.getById(sessionId).getUserId())){
//            throw new BadRequestException("获取该会话记录权限不足");
//        }
        return messageMapper.findBySessionId(sessionId);
    }
}
