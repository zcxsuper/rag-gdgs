package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.domain.entity.LocalFile;
import com.example.exception.BadRequestException;
import com.example.exception.FileStorageException;
import com.example.mapper.LocalFileMapper;
import com.example.service.LocalFileService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class LocalFileServiceImpl extends ServiceImpl<LocalFileMapper, LocalFile> implements LocalFileService {

    private static final String localDir = "/Users/zhao/Desktop/gdgs-file";
    @Override
    public LocalFile getFileById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<LocalFile> getFileListById(Long id) {
        return this.list(new QueryWrapper<LocalFile>()
                .eq(id != null, "parent_id", id));
    }

    @Override
    public void fileDownload(HttpServletResponse response, Long id) throws NotFoundException {
        LocalFile localFile = this.getById(id);
        if (localFile == null) {
            throw new NotFoundException("文件记录不存在, id: " + id);
        }
        Path path = Paths.get(localDir)
                .resolve(localFile.getStorageId())
                .normalize();
        if (!Files.exists(path)) {
            // 数据库有记录，但物理文件丢失了
            throw new FileStorageException("物理文件丢失，无法下载: " + localFile.getStorageId());
        }
        // 清空 response
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(localFile.getName(), StandardCharsets.UTF_8));
        // 二进制流数据
        response.setContentType("application/octet-stream");
        response.setContentLengthLong(localFile.getSize());
        // 将文件读到输入流中
        try (InputStream is = new BufferedInputStream(Files.newInputStream(path))) {
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] buffer = new byte[4096];
            int len;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while((len = is.read(buffer)) > 0){
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new FileStorageException("文件下载失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void fileUpload(MultipartFile file, Long parentId, Long userId) {
        if (file.isEmpty()) {
            throw new BadRequestException("文件不能为空");
        }
        // 获取原始文件名和后缀
        String originalFilename = file.getOriginalFilename();
        // 使用 Spring 的工具类安全地获取文件后缀 (例如 "txt", "png")
        String extension = StringUtils.getFilenameExtension(originalFilename);

        // 生成新的基于 UUID 的文件名 (格式: uuid.后缀)
        String storageId;
        if (StringUtils.hasText(extension)) {
            storageId = UUID.randomUUID() + "." + extension;
        } else {
            storageId = UUID.randomUUID().toString();
        }
        LocalFile localFile = LocalFile.builder()
                .name(originalFilename)
                .parentId(parentId)
                .isFolder(true)
                .createdBy(userId)
                .updatedBy(userId)
                .size(file.getSize())
                .storageId(storageId)
                .build();
        this.saveOrUpdate(localFile);
        Path uploadPath = Paths.get(localDir).toAbsolutePath().normalize();
        // 目标文件完整路径
        Path destinationFile = uploadPath.resolve(storageId).normalize();
        try {
            if (!destinationFile.getParent().equals(uploadPath)) {
                throw new SecurityException("非法的文件路径，存储失败");
            }
            // 如果目录不存在，此行代码会创建它
            Files.createDirectories(uploadPath);
            // 将文件流写入目标文件
            file.transferTo(destinationFile);
        } catch (IOException e) {
            throw new FileStorageException("文件存储失败: " + e.getMessage(), e);
        }
    }
}
