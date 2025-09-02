package com.example.aspect;

import com.example.config.RabbitConfig;
import com.example.domain.ResponseResult;
import com.example.domain.po.Message;
import com.example.enums.AssistantTypeEnum;
import com.example.enums.MessageTypeEnum;
import com.example.enums.SenderTypeEnum;
import com.example.service.MessageService;
import com.example.util.UserContextUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.example.enums.AssistantTypeEnum.LOCAL;

@Aspect
@Component
@Slf4j
public class ChatFlowAspect {

    @Resource
    private RabbitTemplate rabbitTemplate;

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
        String message = (String) args[1];
        log.info("sessionId: {}, message: {}", sessionId, message);

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

