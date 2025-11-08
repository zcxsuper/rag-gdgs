package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domain.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {

    List<Message> getMessageBySessionId(Long sessionId);

    PageDTO<Message> getMessageByPage(Long sessionId, Integer pageNum, Integer pageSize);
}
