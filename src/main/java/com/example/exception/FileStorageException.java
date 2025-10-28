package com.example.exception; // 替换为你的包名

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 文件存储异常
 * 标记为 500 (INTERNAL_SERVER_ERROR)
 * 这是一个运行时异常，因为在文件IO失败时，通常我们无法在业务代码中恢复，
 * 应该上抛给全局异常处理器记录日志并返回 500 错误。
 */

public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}