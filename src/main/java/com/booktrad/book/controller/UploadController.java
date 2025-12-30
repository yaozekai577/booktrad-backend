package com.booktrad.book.controller;

import com.booktrad.book.vo.UploadVO;
import com.booktrad.common.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 文件上传控制器，用于处理图片上传
 * @Date 2025/12/30
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@RestController
@RequestMapping("/api")
public class UploadController {

    /**
     * 允许上传的图片类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png");

    /**
     * 允许上传的文件扩展名
     */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");

    /**
     * 上传文件保存路径
     */
    @Value("${file.upload.path:upload}")
    private String uploadPath;

    /**
     * 服务器访问地址
     */
    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * 图片上传接口
     * @param file 上传的图片文件
     * @return 上传结果，包含图片访问URL
     */
    @PostMapping("/upload/image")
    public Result<UploadVO> uploadImage(MultipartFile file) {
        try {
            // 1. 校验文件是否为空
            if (file == null || file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }

            // 2. 校验文件类型
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
                return Result.error("只支持上传 JPG、JPEG、PNG 格式的图片");
            }

            // 3. 校验文件扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名不能为空");
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return Result.error("只支持上传 JPG、JPEG、PNG 格式的图片");
            }

            // 4. 生成唯一文件名（使用UUID）
            String newFilename = UUID.randomUUID().toString() + "." + extension;

            // 5. 创建上传目录（如果不存在）
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 6. 保存文件到本地
            File destFile = new File(uploadDir, newFilename);
            file.transferTo(destFile);

            // 7. 构建访问URL
            String fileUrl = "http://localhost:" + serverPort + "/upload/" + newFilename;

            // 8. 返回上传结果
            UploadVO uploadVO = new UploadVO();
            uploadVO.setUrl(fileUrl);
            return Result.success("上传成功", uploadVO);

        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("服务器内部错误：" + e.getMessage());
        }
    }
}
