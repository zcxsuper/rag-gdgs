package com.example.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 存储空间信息 VO
 * 用于返回磁盘空间和存储目录占用空间信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageInfoVo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 存储路径
     */
    private String storagePath;
    
    /**
     * 磁盘类型说明
     * - "host": 宿主机物理磁盘（Bind Mount 挂载）
     * - "docker-volume": Docker 命名卷所在磁盘（Named Volume 挂载）
     * - "unknown": 未知类型
     */
    private String diskType;
    
    /**
     * 磁盘总空间（字节）
     * 注意：如果使用 Docker Named Volume，这里显示的是 Docker 数据目录所在磁盘的大小
     * 如果使用 Bind Mount，这里显示的是宿主机物理磁盘的大小
     */
    private Long totalSpace;
    
    /**
     * 磁盘可用空间（字节）
     */
    private Long availableSpace;
    
    /**
     * 磁盘已使用空间（字节）
     */
    private Long usedSpace;
    
    /**
     * 存储目录占用空间（字节）
     */
    private Long directoryUsedSpace;
    
    /**
     * 磁盘使用率（百分比，0-100）
     */
    private Double diskUsagePercent;
    
    /**
     * 存储目录使用率（相对于磁盘可用空间，百分比，0-100）
     */
    private Double directoryUsagePercent;
    
    /**
     * 格式化后的磁盘总空间（如：10.5 GB）
     */
    private String totalSpaceFormatted;
    
    /**
     * 格式化后的磁盘可用空间（如：5.2 GB）
     */
    private String availableSpaceFormatted;
    
    /**
     * 格式化后的磁盘已使用空间（如：5.3 GB）
     */
    private String usedSpaceFormatted;
    
    /**
     * 格式化后的存储目录占用空间（如：1.2 GB）
     */
    private String directoryUsedSpaceFormatted;
}

