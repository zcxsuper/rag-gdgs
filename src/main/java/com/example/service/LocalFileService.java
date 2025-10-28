package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domain.entity.LocalFile;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface LocalFileService extends IService<LocalFile> {

    LocalFile getFileById(Long id);

    List<LocalFile> getFileListById(Long id);

    void fileDownload(HttpServletResponse response, Long id) throws NotFoundException;

    void fileUpload(MultipartFile file, Long parentId, Long userId) throws NotFoundException;
}
