package com.example.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ChatHistoryTools {

//    @Autowired
//    private ChatHistoryRepository chatHistoryRepository;
//
//    @Tool("获取用户聊天历史对话")
//    public List<ChatHistory> getChatHistory(@P("sessionId") String sessionId) {
//        log.info("获取用户与ai聊天历史,sessionId: {}", sessionId);
//        List<ChatHistory> historyList = chatHistoryRepository.findTop21BySessionIdOrderByIdDesc(sessionId)
//                .stream()
//                .sorted(Comparator.comparing(ChatHistoryEntity::getId))
//                .map(chatHistory -> ChatHistory.of(chatHistory.getRole().getRole(), chatHistory.getContent()))
//                .collect(Collectors.toList());
//        // 最新一条记录是用户当前输入的记录，移除掉
//        historyList.remove(historyList.size() - 1);
//        return historyList;
//    }

}

