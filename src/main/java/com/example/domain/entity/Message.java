package com.example.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.enums.AssistantTypeEnum;
import com.example.enums.MessageTypeEnum;
import com.example.enums.SenderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("message")
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long sessionId;

    /** 发送者类型：0=用户，1=人工智能，2=系统 */
    private SenderTypeEnum senderType;

    private MessageTypeEnum messageType;

    /** 0 本地 1联网*/
    private AssistantTypeEnum assistantType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime created;

    private String contents;
}
