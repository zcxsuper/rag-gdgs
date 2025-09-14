package com.example.aspect;

import com.example.config.RabbitConfig;
import com.example.domain.ResponseResult;
import com.example.domain.po.Message;
import com.example.enums.AssistantTypeEnum;
import com.example.enums.MessageTypeEnum;
import com.example.enums.SenderTypeEnum;
import com.example.exception.BadRequestException;
import com.example.service.SessionService;
import com.example.util.UserContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper; // Jackson
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

import static com.example.enums.AssistantTypeEnum.LOCAL;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatFlowAspect {


    private final RabbitTemplate rabbitTemplate;
    private final SessionService sessionService;

    private void sendMessage(Message message) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.MESSAGE_EXCHANGE,
                RabbitConfig.MESSAGE_ROUTING_KEY,
                message
        );
    }

    @Pointcut("@annotation(com.example.annotation.ChatFlow)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 输入参数
        Object[] args = joinPoint.getArgs();
        String session = (String) args[0];
        long sessionId;
        String assistantType = String.valueOf(LOCAL);
        if (!session.contains(":")) {
            sessionId = Long.parseLong(session);
        } else {
            String[] split = session.split(":");
            sessionId = Long.parseLong(split[0]);
            assistantType = split[1];
        }

        String json = (String) args[1];
        ObjectMapper mapper = new ObjectMapper();
        String message = (String) mapper.readValue(json, Map.class).get("message");

        log.info("sessionId: {}, message: {}", sessionId, message);

        Long userId = UserContextUtil.getUserId();
        if(!userId.equals(sessionService.getById(sessionId).getUserId())){
            throw new BadRequestException("会话权限不足");
        }

        Message messageUser = Message.builder()
                .sessionId(sessionId)
                .senderType(SenderTypeEnum.USER)
                .messageType(MessageTypeEnum.TEXT)
                .createTime(LocalDateTime.now())
                .contents(message)
                .assistantType(AssistantTypeEnum.valueOf(assistantType))
                .build();
        sendMessage(messageUser);

        Object result = joinPoint.proceed();

        if (result instanceof ResponseResult<?> response) {
            log.info("ChatFlow 输出: code={}, msg={}, data={}",
                    response.getCode(), response.getMsg(), response.getData());

            Message messageAi = Message.builder()
                    .sessionId(sessionId)
                    .senderType(SenderTypeEnum.AI)
                    .messageType(MessageTypeEnum.TEXT)
                    .createTime(LocalDateTime.now())
                    .contents(response.getData().toString())
                    .assistantType(AssistantTypeEnum.valueOf(assistantType))
                    .build();
            sendMessage(messageAi);
        }
        return result;
    }
}

