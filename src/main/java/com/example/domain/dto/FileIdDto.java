package com.example.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 文件ID参数DTO
 * 用于路径参数和查询参数的校验
 */
@Data
public class FileIdDto {

    @NotNull(message = "文件ID不能为空")
    private Long id;
}

