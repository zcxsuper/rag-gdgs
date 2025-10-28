package com.example.controller;

import com.example.domain.ResponseResult;
import com.example.domain.entity.LocalFile;
import com.example.exception.BadRequestException;
import com.example.exception.FileStorageException;
import com.example.service.LocalFileService;
import com.example.util.UserContextUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class LocalFileController {

    private final LocalFileService localFileService;

    //    @GetMapping("/file/download")
//    public void fileDownload(HttpServletResponse response, @RequestParam("filePath") String filePath) {
//        File file = new File(filePath);
//        if (!file.exists()) {
//            throw new IllegalArgumentException("当前下载的文件不存在，请检查路径是否正确");
//        }
//
//        // 将文件写入输入流
//        try (InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
//
//            // 一次性读取到内存中
//            byte[] buffer = new byte[is.available()];
//            int read = is.read(buffer);
//
//            // 清空 response
//            response.reset();
//            response.setCharacterEncoding("UTF-8");
//
//            // Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
//            // attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
//            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
//            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
//
//            // 告知浏览器文件的大小
//            response.addHeader("Content-Length", "" + file.length());
//
//            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
//            response.setContentType("application/octet-stream");
//            outputStream.write(buffer);
//            outputStream.flush();
//            outputStream.close();
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//    @GetMapping("/net/download")
//    public void netDownload(HttpServletResponse response, @RequestParam("fileAddress") String fileAddress, @RequestParam("filename") String filename) {
//
//        try {
//            URL url = new URL(fileAddress);
//            URLConnection conn = url.openConnection();
//            InputStream inputStream = conn.getInputStream();
//
//            response.reset();
//            response.setContentType(conn.getContentType());
//
//            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
//
//            byte[] buffer = new byte[1024];
//            int len;
//
//            OutputStream outputStream = response.getOutputStream();
//
//            while ((len = inputStream.read(buffer)) > 0) {
//                outputStream.write(buffer, 0, len);
//            }
//
//            inputStream.close();
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

    @GetMapping("/download")
    public void fileDownload(HttpServletResponse response, @RequestParam("id") Long id) throws NotFoundException {
        localFileService.fileDownload(response, id);
    }

    /**
     * 文件上传处理
     * @param file 上传的文件 (来自表单的 'file' 字段)
     * @param parentId 文件的父目录ID (可选, null表示根目录)
     * @return 包含新文件元数据的 ResponseEntity
     */
    @PostMapping("/upload")
    public ResponseResult<T> fileUpload(@RequestParam("file") MultipartFile file,
                                        @RequestParam(value = "parent-id", required = false) Long parentId) throws NotFoundException {
        Long userId = UserContextUtil.getUserId();
        localFileService.fileUpload(file, parentId, userId);
        return ResponseResult.success();
    }



//    /** 上传或创建文件记录 */
//    @PostMapping("/create")
//    public boolean createFile(@RequestBody Files file) {
//        return filesService.save(file);
//    }



//    /** 更新文件信息 */
//    @PutMapping("/update")
//    public boolean updateFile(@RequestBody Files file) {
//        return fileService.updateById(file);
//    }


//    /** 删除文件 */
//    @DeleteMapping("/{id}")
//    public boolean deleteFile(@PathVariable Long id) {
//        return fileService.removeById(id);
//    }


    /** 根据ID查询文件 */
    @GetMapping("/{id}")
    public LocalFile getFileById(@PathVariable Long id) {
        return localFileService.getFileById(id);
    }

    /** 查询某个目录下的所有文件 */
    @GetMapping("/list")
    public List<LocalFile> listFiles(@RequestParam(required = false) Long id) {
        return localFileService.getFileListById(id);
    }

}
