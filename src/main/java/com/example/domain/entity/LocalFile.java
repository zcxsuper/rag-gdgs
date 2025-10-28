package com.example.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文件信息实体类，对应数据库表 files
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("files")
public class LocalFile {

    /** 文件ID，主键 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 文件名（含扩展名） */
    private String name;

    /** 父目录ID（null 表示根目录） */
    private Long parentId;

    /** 是否为文件夹 */
    private Boolean isFolder;

    /** 文件大小（字节） */
    private Long size;

    private Long createdBy;

    private Long updatedBy;

    /** 上传时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime created;

    /** 最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updated;

    /** 文件在服务器的存储位置（相对路径或唯一存储ID） */
    @Builder.Default
    private String storageId = UUID.randomUUID().toString();
}
