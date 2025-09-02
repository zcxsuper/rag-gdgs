package com.example.advice;

import com.example.exception.BadRequestException;
import com.example.exception.TokenInvalidException;
import com.example.exception.UnauthorizedException;
import com.example.exception.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 TokenException
     */
    @ExceptionHandler(BadRequestException.class)
    public Map<String, Object> handleRequestException(BadRequestException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.BAD_REQUEST.value()); // 400
        response.put("msg", e.getMessage());
        return response;
    }

    @ExceptionHandler(TokenInvalidException.class)
    public Map<String, Object> handleTokenException(TokenInvalidException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.UNAUTHORIZED.value()); // 401
        response.put("msg", e.getMessage());
        return response;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Map<String, Object> handleAuthorizedException(UnauthorizedException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.UNAUTHORIZED.value()); // 401
        response.put("msg", e.getMessage());
        return response;
    }

    @ExceptionHandler(UserException.class)
    public Map<String, Object> handleUserException(UserException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.BAD_REQUEST.value()); // 400
        response.put("msg", e.getMessage());
        return response;
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleOtherException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value()); // 500
        response.put("msg", "服务器内部错误: " + e.getMessage());
        return response;
    }
}
