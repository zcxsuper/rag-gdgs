package com.example.exception;

import com.example.domain.ResultCode;

/**
 * 自定义 Token 异常
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
