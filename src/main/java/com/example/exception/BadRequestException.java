package com.example.exception;

import com.example.domain.ResultCode;

/**
 * 自定义请求异常（400 Bad Request）
 * 用于处理客户端请求参数错误、验证失败等情况
 */
public class BadRequestException extends RuntimeException {

    private final int code;

    public BadRequestException(String message) {
        super(message);
        this.code = ResultCode.BAD_REQUEST;
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.BAD_REQUEST;
    }

    public BadRequestException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
