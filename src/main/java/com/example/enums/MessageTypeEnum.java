package com.example.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


@Getter
public enum MessageTypeEnum {

    TEXT(0, "文本"),
    IMAGE(1, "图片"),
    AUDIO(2, "音频"),
    FILE(3, "文件"),
    JSON(4, "JSON");

    @EnumValue
    @JsonValue
    private final int code;
    private final String description;

    MessageTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据 code 获取枚举
     */
    public static MessageTypeEnum fromCode(int code) {
        for (MessageTypeEnum type : MessageTypeEnum.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知消息类型: " + code);
    }
}
