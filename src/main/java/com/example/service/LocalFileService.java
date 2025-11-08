package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domain.dto.FolderDto;
import com.example.domain.entity.LocalFile;
import com.example.domain.vo.StorageInfoVo;
import jakarta.servlet.http.HttpServletResponse;
import com.example.exception.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface LocalFileService extends IService<LocalFile> {

    LocalFile getFileById(Long id);

    List<LocalFile> getFileListById(Long id);

    void fileDownload(HttpServletResponse response, Long id) throws NotFoundException;

    void fileUpload(MultipartFile file, Long parentId, Long userId) throws NotFoundException;

    void createFolder(FolderDto folderDto, Long userId) throws NotFoundException;

    void renameFile(Long id, String newName, Long userId) throws NotFoundException;

    void deleteFile(Long id, Long userId) throws NotFoundException;

    void moveFile(Long id, Long newParentId, Long userId) throws NotFoundException;

    /**
     * 获取存储空间信息
     * 包括磁盘总空间、可用空间、已使用空间，以及存储目录的占用空间
     * @return 存储空间信息
     */
    StorageInfoVo getStorageInfo();
}
