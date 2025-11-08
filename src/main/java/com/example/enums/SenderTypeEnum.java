package com.example.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SenderTypeEnum {
    USER(0, "用户"),
    AI(1, "人工智能"),
    SYSTEM(2, "系统");

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;

    SenderTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 通过 code 获取枚举
    public static SenderTypeEnum fromCode(int code) {
        for (SenderTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知发送者类型: " + code);
    }
}
