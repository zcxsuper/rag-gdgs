package com.example.service;

import com.example.util.UserContextUtil;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

// 手动指定
@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
//        chatModel = "openAiChatModel",
        streamingChatModel = "openAiStreamingChatModel",
        // chatMemory = "chatMemory", // 配置会话记忆对象 
        chatMemoryProvider = "chatMemoryProvider" // 配置会话记忆提供者对象
//        contentRetriever = "contentRetriever" // 配置向量数据库检索对象
//        tools = "reservationTool"
)
// @AiService
public interface Assistant {

    // @SystemMessage("")
    Flux<String> chat(@MemoryId String memoryId, @UserMessage String message);
//    String chat(@MemoryId String session, @UserMessage String message);
}
