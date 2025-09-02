package com.example.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum AssistantTypeEnum {
    LOCAL(0, "本地助手"),
    ONLINE(1, "联网助手");

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;

    AssistantTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 通过 code 获取枚举
    public static AssistantTypeEnum fromCode(int code) {
        for (AssistantTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知助手类型: " + code);
    }
}