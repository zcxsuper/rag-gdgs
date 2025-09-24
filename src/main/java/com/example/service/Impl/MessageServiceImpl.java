package com.example.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.po.Message;
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
    public PageDTO<Message> getMessageByPage(Integer pageNum, Integer pageSize) {
        // 分页查询
        Page<Message> page = this.lambdaQuery().page(new Page<>(pageNum, pageSize));

        // 获取分页记录
        List<Message> records = page.getRecords();

        // 如果没有记录，返回空的 PageDTO
        if (records.isEmpty()) {
            return new PageDTO<>();
        }

        // 用 PageDTO 包装
        PageDTO<Message> result = new PageDTO<>(pageNum, pageSize, page.getTotal());
        result.setRecords(records);
        return result;
    }
}
