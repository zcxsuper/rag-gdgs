package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.domain.dto.FolderDto;
import com.example.domain.entity.LocalFile;
import com.example.domain.vo.StorageInfoVo;
import com.example.exception.BadRequestException;
import com.example.exception.FileStorageException;
import com.example.mapper.LocalFileMapper;
import com.example.service.LocalFileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import com.example.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
public class LocalFileServiceImpl extends ServiceImpl<LocalFileMapper, LocalFile> implements LocalFileService {

    @Value("${file.storage-path}")
    private String storagePath;
    
    // 文件名非法字符正则表达式
    private static final Pattern INVALID_FILENAME_CHARS = Pattern.compile("[\\\\/:*?\"<>|]");
    
    // 文件名最大长度（对应数据库varchar(255)限制）
    private static final int MAX_FILENAME_LENGTH = 255;
    
    // 文件大小限制：100MB (100 * 1024 * 1024 字节)
    private static final long MAX_FILE_SIZE = 100L * 1024 * 1024;
    
    // 递归检查最大深度，防止无限递归
    private static final int MAX_RECURSION_DEPTH = 100;

    @Override
    public LocalFile getFileById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("文件ID无效");
        }
        return this.getById(id);
    }

    @Override
    public List<LocalFile> getFileListById(Long id) {
        // 如果提供了id，验证父文件夹是否存在
        if (id != null) {
            if (id <= 0) {
                throw new BadRequestException("文件夹ID无效");
            }
            LocalFile parent = this.getById(id);
            if (parent == null) {
                throw new NotFoundException(String.format("父文件夹不存在, id: %d", id));
            }
            if (!Boolean.TRUE.equals(parent.getFolder())) {
                throw new BadRequestException(String.format("指定的ID不是一个文件夹, id: %d", id));
            }
        }
        
        QueryWrapper<LocalFile> wrapper = new QueryWrapper<>();
        if (id == null) {
            wrapper.isNull("parent_id"); // parent_id 为 NULL 的记录
        } else {
            wrapper.eq("parent_id", id); // parent_id = id
        }

        return this.list(wrapper);
    }


    @Override
    public void fileDownload(HttpServletResponse response, Long id) throws NotFoundException {
        // 参数验证
        if (id == null || id <= 0) {
            throw new BadRequestException("文件ID无效");
        }
        if (response == null) {
            throw new BadRequestException("响应对象不能为空");
        }
        
        LocalFile localFile = this.getById(id);
        if (localFile == null) {
            throw new NotFoundException(String.format("文件记录不存在, id: %d", id));
        }
        
        // 检查是否是文件夹（文件夹不能下载）
        if (Boolean.TRUE.equals(localFile.getFolder())) {
            throw new BadRequestException(String.format("不能下载文件夹, id: %d", id));
        }
        
        // 检查storageId是否有效
        if (!StringUtils.hasText(localFile.getStorageId())) {
            throw new FileStorageException(String.format("文件存储ID无效, id: %d", id));
        }
        
        Path uploadPath = Paths.get(storagePath).toAbsolutePath().normalize();
        Path path = uploadPath.resolve(localFile.getStorageId()).normalize();
        
        // 安全检查：确保文件路径在允许的目录内
        if (!path.startsWith(uploadPath)) {
            throw new FileStorageException(String.format("文件路径不安全，无法下载: %s", localFile.getStorageId()));
        }
        
        if (!Files.exists(path)) {
            // 数据库有记录，但物理文件丢失了
            throw new FileStorageException(String.format("物理文件丢失，无法下载: %s", localFile.getStorageId()));
        }
        
        // 检查是否是目录（不应该发生，但为了安全）
        if (Files.isDirectory(path)) {
            throw new FileStorageException(String.format("指定的路径是一个目录，无法下载: %s", localFile.getStorageId()));
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
            while ((len = is.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new FileStorageException(String.format("文件下载失败: %s", e.getMessage()), e);
        }
    }

    @Override
    public void fileUpload(MultipartFile file, Long parentId, Long userId) throws NotFoundException {
        // 参数验证
        if (file == null) {
            throw new BadRequestException("文件对象不能为空");
        }
        if (file.isEmpty()) {
            throw new BadRequestException("文件不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("用户ID无效");
        }
        
        // 检查文件大小
        long fileSize = file.getSize();
        if (fileSize <= 0) {
            throw new BadRequestException("文件大小无效");
        }
        if (fileSize > MAX_FILE_SIZE) {
            throw new BadRequestException(String.format("文件大小超过限制，最大允许 %dMB", MAX_FILE_SIZE / 1024 / 1024));
        }
        
        // 获取原始文件名和后缀
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new BadRequestException("文件名不能为空");
        }
        
        // 验证文件名合法性（包括长度检查，数据库限制为varchar(255)）
        validateFileName(originalFilename);
        
        // 如果提供了parentId，验证父文件夹是否存在
        if (parentId != null) {
            if (parentId <= 0) {
                throw new BadRequestException("父文件夹ID无效");
            }
            LocalFile parent = this.getById(parentId);
            if (parent == null) {
                throw new NotFoundException(String.format("父文件夹不存在, id: %d", parentId));
            }
            if (!Boolean.TRUE.equals(parent.getFolder())) {
                throw new BadRequestException(String.format("指定的父ID不是一个文件夹, id: %d", parentId));
            }
        }
        
        // 检查同级目录下是否已存在同名文件或文件夹
        checkDuplicateName(parentId, originalFilename, null);
        
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
                .folder(false)
                .createdBy(userId)
                .updatedBy(userId)
                .size(file.getSize())
                .storageId(storageId)
                .build();
        // 保存数据库记录
        if (!this.saveOrUpdate(localFile)) {
            throw new FileStorageException("保存文件记录到数据库失败");
        }
        
        Path uploadPath = Paths.get(storagePath).toAbsolutePath().normalize();
        // 目标文件完整路径
        Path destinationFile = uploadPath.resolve(storageId).normalize();
        
        try {
            // 安全检查：确保文件路径在允许的目录内
            if (!destinationFile.getParent().equals(uploadPath)) {
                // 如果路径不安全，删除已创建的数据库记录
                this.removeById(localFile.getId());
                throw new FileStorageException("非法的文件路径，存储失败");
            }
            
            // 如果目录不存在，创建它
            Files.createDirectories(uploadPath);
            
            // 检查磁盘空间（简单检查，实际应该检查可用空间）
            if (Files.exists(uploadPath)) {
                try {
                    long usableSpace = uploadPath.toFile().getUsableSpace();
                    if (usableSpace < fileSize) {
                        // 删除已创建的数据库记录
                        this.removeById(localFile.getId());
                        throw new FileStorageException("磁盘空间不足，无法存储文件");
                    }
                } catch (Exception e) {
                    log.warn("无法检查磁盘空间: {}", e.getMessage());
                }
            }
            
            // 将文件流写入目标文件
            file.transferTo(destinationFile);
            
            // 验证文件是否成功写入
            if (!Files.exists(destinationFile) || Files.size(destinationFile) != fileSize) {
                // 删除已创建的数据库记录
                this.removeById(localFile.getId());
                // 尝试删除不完整的文件
                try {
                    Files.deleteIfExists(destinationFile);
                } catch (IOException ignored) {
                }
                throw new FileStorageException("文件写入不完整，存储失败");
            }
        } catch (FileStorageException e) {
            throw e;
        } catch (IOException e) {
            // 删除已创建的数据库记录
            try {
                this.removeById(localFile.getId());
            } catch (Exception ignored) {
            }
            throw new FileStorageException(String.format("文件存储失败: %s", e.getMessage()), e);
        }
    }

    @Override
    public void createFolder(FolderDto folderDto, Long userId) throws NotFoundException {
        // 参数验证
        if (folderDto == null) {
            throw new BadRequestException("文件夹信息不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("用户ID无效");
        }
        
        // 验证文件夹名称合法性（包括长度检查，数据库限制为varchar(255)）
        if (folderDto.getName() == null || !StringUtils.hasText(folderDto.getName())) {
            throw new BadRequestException("文件夹名称不能为空");
        }
        validateFileName(folderDto.getName());
        
        // 如果提供了parentId，验证父文件夹是否存在
        if (folderDto.getParentId() != null) {
            if (folderDto.getParentId() <= 0) {
                throw new BadRequestException("父文件夹ID无效");
            }
            LocalFile parent = this.getById(folderDto.getParentId());
            if (parent == null) {
                throw new NotFoundException(String.format("父文件夹不存在, id: %d", folderDto.getParentId()));
            }
            if (!Boolean.TRUE.equals(parent.getFolder())) {
                throw new BadRequestException(String.format("指定的父ID不是一个文件夹, id: %d", folderDto.getParentId()));
            }
        }
        
        // 检查同级目录下是否已存在同名文件或文件夹
        checkDuplicateName(folderDto.getParentId(), folderDto.getName(), null);
        
        LocalFile localFile = LocalFile.builder()
                .name(folderDto.getName())
                .parentId(folderDto.getParentId())
                .folder(true)
                .createdBy(userId)
                .updatedBy(userId)
                .size(0L)
                .build();
        if (!this.saveOrUpdate(localFile)) {
            throw new FileStorageException(String.format("文件夹创建失败: %s", folderDto.getName()));
        }
    }

    @Override
    public void renameFile(Long id, String newName, Long userId) throws NotFoundException {
        // 参数验证
        if (id == null || id <= 0) {
            throw new BadRequestException("文件ID无效");
        }
        if (!StringUtils.hasText(newName)) {
            throw new BadRequestException("新文件名不能为空");
        }
        
        // 验证文件名合法性
        validateFileName(newName);
        
        LocalFile localFile = this.getById(id);
        if (localFile == null) {
            throw new NotFoundException("文件或文件夹不存在, id: " + id);
        }
        
        // 检查新名称是否与当前名称相同
        if (newName.equals(localFile.getName())) {
            throw new BadRequestException("新文件名与当前文件名相同，无需重命名");
        }
        
        // 检查同一父目录下是否已存在同名文件或文件夹
        checkDuplicateName(localFile.getParentId(), newName, id);
        
        localFile.setName(newName.trim());
        localFile.setUpdatedBy(userId);
        if (!this.updateById(localFile)) {
            throw new FileStorageException("重命名失败: " + id);
        }
    }

    @Override
    public void deleteFile(Long id, Long userId) throws NotFoundException {
        // 参数验证
        if (id == null || id <= 0) {
            throw new BadRequestException("文件ID无效");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("用户ID无效");
        }
        
        LocalFile localFile = this.getById(id);
        if (localFile == null) {
            throw new NotFoundException("文件或文件夹不存在, id: " + id);
        }
        
        try {
            // 如果是文件夹，递归删除所有子文件和子文件夹
            if (Boolean.TRUE.equals(localFile.getFolder())) {
                deleteFolderRecursively(id);
            } else {
                // 如果是文件，删除物理文件
                deletePhysicalFile(localFile);
            }
            
            // 删除数据库记录
            if (!this.removeById(id)) {
                throw new FileStorageException("删除文件记录失败: " + id);
            }
        } catch (Exception e) {
            // 如果是我们自定义的异常，直接抛出
            if (e instanceof BadRequestException || 
                e instanceof NotFoundException || 
                e instanceof FileStorageException) {
                throw e;
            }
            // 其他异常包装后抛出
            throw new FileStorageException("删除文件或文件夹时发生错误: " + e.getMessage(), e);
        }
    }

    @Override
    public void moveFile(Long id, Long newParentId, Long userId) throws NotFoundException {
        // 参数验证
        if (id == null || id <= 0) {
            throw new BadRequestException("文件ID无效");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("用户ID无效");
        }

        LocalFile localFile = this.getById(id);
        if (localFile == null) {
            throw new NotFoundException("文件或文件夹不存在, id: " + id);
        }
        
        // 检查是否移动到同一位置（无需移动）
        Long currentParentId = localFile.getParentId();
        if ((newParentId == null && currentParentId == null) || 
            (newParentId != null && newParentId.equals(currentParentId))) {
            throw new BadRequestException("文件或文件夹已在目标位置，无需移动");
        }
        
        // 检查是否移动到自己或子文件夹中（避免循环引用）
        if (newParentId != null && newParentId.equals(id)) {
            throw new BadRequestException("不能将文件或文件夹移动到自身");
        }
        
        if (newParentId != null) {
            // 检查新父文件夹是否存在
            LocalFile newParent = this.getById(newParentId);
            if (newParent == null) {
                throw new NotFoundException("目标父文件夹不存在, id: " + newParentId);
            }
            if (!Boolean.TRUE.equals(newParent.getFolder())) {
                throw new BadRequestException("目标不是一个文件夹, id: " + newParentId);
            }
            
            // 检查是否会形成循环引用（即移动的文件夹是否是目标文件夹的祖先）
            if (isAncestor(id, newParentId)) {
                throw new BadRequestException("不能将文件夹移动到其子文件夹中");
            }
            
            // 检查目标位置是否已存在同名文件或文件夹
            checkDuplicateName(newParentId, localFile.getName(), id);
        } else {
            // 移动到根目录，检查根目录下是否已存在同名文件或文件夹
            checkDuplicateName(null, localFile.getName(), id);
        }
        
        localFile.setParentId(newParentId);
        localFile.setUpdatedBy(userId);
        if (!this.updateById(localFile)) {
            throw new FileStorageException("移动文件失败: " + id);
        }
    }

    private void deleteFolderRecursively(Long folderId) {
        // 查询该文件夹下的所有子文件和子文件夹
        QueryWrapper<LocalFile> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", folderId);
        List<LocalFile> children = this.list(wrapper);
        
        if (children == null || children.isEmpty()) {
            return;
        }
        
        // 递归处理每个子项
        for (LocalFile child : children) {
            if (child == null || child.getId() == null) {
                continue; // 跳过无效数据
            }
            
            try {
                if (Boolean.TRUE.equals(child.getFolder())) {
                    // 如果是文件夹，递归删除
                    deleteFolderRecursively(child.getId());
                } else {
                    // 如果是文件，删除物理文件
                    deletePhysicalFile(child);
                }
                
                // 删除数据库记录
                boolean deleted = this.removeById(child.getId());
                if (!deleted) {
                    // 记录警告，但继续删除其他文件
                    log.warn("删除数据库记录失败, id: {}", child.getId());
                }
            } catch (Exception e) {
                // 记录错误，但继续删除其他文件
                log.error("删除文件或文件夹时发生错误, id: {}, 错误: {}", child.getId(), e.getMessage(), e);
            }
        }
    }

  
    private void deletePhysicalFile(LocalFile localFile) {
        if (localFile == null) {
            return;
        }
        
        try {
            Path uploadPath = Paths.get(storagePath).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(localFile.getStorageId()).normalize();
            
            // 安全检查：确保文件路径在允许的目录内
            if (!filePath.startsWith(uploadPath)) {
                log.warn("文件路径不安全，跳过删除: {}", filePath);
                return;
            }
            
            if (Files.exists(filePath)) {
                // 如果是目录，需要先删除目录内容
                if (Files.isDirectory(filePath)) {
                    // 理论上文件夹不应该有物理存储路径，但为了安全起见处理这种情况
                    Files.delete(filePath);
                } else {
                    Files.delete(filePath);
                }
            }
        } catch (IOException e) {
            // 记录日志，但不阻止删除数据库记录
            log.error("删除物理文件失败, storageId: {}, 错误: {}", localFile.getStorageId(), e.getMessage(), e);
            // 对于某些错误（如文件被占用），可以选择抛出异常
            if (e.getMessage() != null && e.getMessage().contains("被使用")) {
                throw new FileStorageException("文件正在被使用，无法删除: " + localFile.getName(), e);
            }
        } catch (Exception e) {
            log.error("删除物理文件时发生未知错误, storageId: {}, 错误: {}", localFile.getStorageId(), e.getMessage(), e);
        }
    }

    /**
     * 检查folderId是否是targetId的祖先（用于防止循环引用）
     * @param folderId 要移动的文件夹ID
     * @param targetId 目标父文件夹ID
     * @return true表示folderId是targetId的祖先
     */
    private boolean isAncestor(Long folderId, Long targetId) {
        return isAncestor(folderId, targetId, 0);
    }
    
    /**
     * 检查folderId是否是targetId的祖先（带深度限制，防止无限递归）
     */
    private boolean isAncestor(Long folderId, Long targetId, int depth) {
        // 防止无限递归
        if (depth > MAX_RECURSION_DEPTH) {
            log.warn("递归深度超过限制，可能存在循环引用, folderId: {}, targetId: {}", folderId, targetId);
            return true; // 超过深度限制，认为存在循环引用，阻止操作
        }
        
        if (folderId == null || targetId == null || folderId.equals(targetId)) {
            return false;
        }
        
        LocalFile target = this.getById(targetId);
        if (target == null || target.getParentId() == null) {
            return false;
        }
        
        if (target.getParentId().equals(folderId)) {
            return true;
        }
        
        // 递归检查父级
        return isAncestor(folderId, target.getParentId(), depth + 1);
    }
    
    private void validateFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            throw new BadRequestException("文件名不能为空");
        }
        
        String trimmedName = fileName.trim();
        if (trimmedName.isEmpty()) {
            throw new BadRequestException("文件名不能只包含空格");
        }
        
        // 检查文件名长度（数据库字段限制为varchar(255)）
        if (trimmedName.length() > MAX_FILENAME_LENGTH) {
            throw new BadRequestException("文件名长度不能超过 " + MAX_FILENAME_LENGTH + " 个字符（数据库限制）");
        }
        
        // 检查非法字符
        if (INVALID_FILENAME_CHARS.matcher(trimmedName).find()) {
            throw new BadRequestException("文件名包含非法字符，不允许使用: \\ / : * ? \" < > |");
        }
        
        // 检查文件名不能以点开头或结尾（特殊情况除外）
        if (trimmedName.startsWith(".") && trimmedName.length() == 1) {
            throw new BadRequestException("文件名不能只是一个点");
        }
        if (trimmedName.endsWith(".") && !trimmedName.equals("..")) {
            throw new BadRequestException("文件名不能以点结尾");
        }
    }
    

    private void checkDuplicateName(Long parentId, String name, Long excludeId) {
        if (!StringUtils.hasText(name)) {
            return;
        }
        
        QueryWrapper<LocalFile> wrapper = new QueryWrapper<>();
        if (parentId == null) {
            wrapper.isNull("parent_id");
        } else {
            wrapper.eq("parent_id", parentId);
        }
        wrapper.eq("name", name.trim());
        
        // 排除当前文件（用于重命名和移动时的检查）
        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }
        
        long count = this.count(wrapper);
        if (count > 0) {
            throw new BadRequestException("同一目录下已存在同名文件或文件夹: " + name);
        }
    }

    @Override
    public StorageInfoVo getStorageInfo() {
        try {
            Path uploadPath = Paths.get(storagePath).toAbsolutePath().normalize();
            
            // 确保目录存在
            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                } catch (IOException e) {
                    log.error("创建存储目录失败: {}", uploadPath, e);
                    throw new FileStorageException("创建存储目录失败: " + e.getMessage(), e);
                }
            }
            
            // 获取文件存储系统信息
            FileStore store = Files.getFileStore(uploadPath);
            
            // 检测磁盘类型（尝试判断是宿主机磁盘还是 Docker 卷）
            String diskType = detectDiskType(uploadPath, store);
            
            // 获取磁盘空间信息
            long totalSpace = store.getTotalSpace();
            long usableSpace = store.getUsableSpace(); // 可用空间（考虑权限）
            long usedSpace = totalSpace - usableSpace;
            
            // 计算存储目录的占用空间
            long directoryUsedSpace = calculateDirectorySize(uploadPath);
            
            // 计算使用率
            double diskUsagePercent = totalSpace > 0 ? (double) usedSpace / totalSpace * 100 : 0;
            double directoryUsagePercent = usableSpace > 0 ? (double) directoryUsedSpace / usableSpace * 100 : 0;
            
            // 构建返回对象
            return StorageInfoVo.builder()
                    .storagePath(uploadPath.toString())
                    .diskType(diskType)
                    .totalSpace(totalSpace)
                    .availableSpace(usableSpace)
                    .usedSpace(usedSpace)
                    .directoryUsedSpace(directoryUsedSpace)
                    .diskUsagePercent(Math.round(diskUsagePercent * 100.0) / 100.0)
                    .directoryUsagePercent(Math.round(directoryUsagePercent * 100.0) / 100.0)
                    .totalSpaceFormatted(formatFileSize(totalSpace))
                    .availableSpaceFormatted(formatFileSize(usableSpace))
                    .usedSpaceFormatted(formatFileSize(usedSpace))
                    .directoryUsedSpaceFormatted(formatFileSize(directoryUsedSpace))
                    .build();
                    
        } catch (IOException e) {
            log.error("获取存储空间信息失败", e);
            throw new FileStorageException("获取存储空间信息失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("获取存储空间信息时发生未知错误", e);
            throw new FileStorageException("获取存储空间信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检测磁盘类型
     * 尝试判断是宿主机物理磁盘（Bind Mount）还是 Docker 命名卷（Named Volume）
     * @param path 存储路径
     * @param store 文件存储系统
     * @return 磁盘类型：host（宿主机）、docker-volume（Docker 卷）、unknown（未知）
     */
    private String detectDiskType(Path path, FileStore store) {
        try {
            String pathStr = path.toString();
            String storeType = store.type();
            
            // 检查是否是 Docker 卷路径（通常在 /var/lib/docker/volumes/ 下）
            if (pathStr.contains("/var/lib/docker/volumes/") || 
                pathStr.contains("\\var\\lib\\docker\\volumes\\")) {
                return "docker-volume";
            }
            
            // 检查是否是 Docker Desktop 的卷路径（macOS/Windows）
            if (pathStr.contains("/mnt/wsl/") || 
                pathStr.contains("\\wsl\\") ||
                pathStr.contains("/mnt/host/")) {
                return "docker-volume";
            }
            
            // 检查文件系统类型
            if (storeType != null) {
                String lowerType = storeType.toLowerCase();
                // Docker 卷通常使用 overlay、overlay2、zfs 等文件系统
                if (lowerType.contains("overlay") || 
                    lowerType.contains("zfs") ||
                    lowerType.contains("btrfs")) {
                    // 进一步检查路径，避免误判
                    if (!pathStr.startsWith("/") && !pathStr.matches("^[A-Z]:\\\\")) {
                        // 相对路径，可能是 Docker 卷
                        return "docker-volume";
                    }
                }
            }
            
            // 检查是否是常见的宿主机路径
            if (pathStr.startsWith("/") || pathStr.matches("^[A-Z]:\\\\")) {
                // 绝对路径，通常是宿主机路径（Bind Mount）
                // 但需要排除 Docker 卷路径
                if (!pathStr.contains("/var/lib/docker/") && 
                    !pathStr.contains("\\var\\lib\\docker\\")) {
                    return "host";
                }
            }
            
            // 默认返回 unknown
            return "unknown";
        } catch (Exception e) {
            log.warn("检测磁盘类型失败: {}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * 递归计算目录大小
     * @param directory 目录路径
     * @return 目录大小（字节）
     */
    private long calculateDirectorySize(Path directory) {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return 0;
        }
        
        long size = 0;
        try {
            // 使用 Files.walk 遍历目录，限制深度防止无限递归和符号链接问题
            // 设置最大深度为 100，防止过深嵌套
            size = Files.walk(directory, 100)
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        // 跳过符号链接，避免循环引用
                        try {
                            return !Files.isSymbolicLink(path);
                        } catch (SecurityException e) {
                            log.warn("无权限检查符号链接: {}", path, e);
                            return false;
                        }
                    })
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                            log.warn("无法获取文件大小: {}", path, e);
                            return 0;
                        } catch (SecurityException e) {
                            log.warn("无权限访问文件: {}", path, e);
                            return 0;
                        }
                    })
                    .sum();
        } catch (IOException e) {
            log.error("计算目录大小失败: {}", directory, e);
        } catch (SecurityException e) {
            log.error("无权限访问目录: {}", directory, e);
        } catch (Exception e) {
            log.error("计算目录大小时发生未知错误: {}", directory, e);
        }
        
        return size;
    }

    /**
     * 格式化文件大小
     * @param size 文件大小（字节）
     * @return 格式化后的字符串（如：1.5 GB, 500 MB）
     */
    private String formatFileSize(long size) {
        if (size < 0) {
            return "0 B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB", "PB"};
        int unitIndex = 0;
        double fileSize = size;
        
        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(fileSize) + " " + units[unitIndex];
    }
}
