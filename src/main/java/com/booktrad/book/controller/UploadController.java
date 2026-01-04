package com.booktrad.book.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.booktrad.book.vo.UploadVO;
import com.booktrad.common.result.Result;
import com.booktrad.config.OssConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
     * OSS配置
     */
    @Autowired
    private OssConfig ossConfig;

    /**
     * OSS客户端
     */
    @Autowired
    private OSS ossClient;

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

            // 5. 构建OSS文件路径
            String ossFilePath = ossConfig.getPrefix() + newFilename;

            // 6. 获取文件输入流
            try (InputStream inputStream = file.getInputStream()) {
                // 7. 创建ObjectMetadata对象，用于设置文件元数据
                com.aliyun.oss.model.ObjectMetadata objectMetadata = new com.aliyun.oss.model.ObjectMetadata();
                // 设置文件ContentType
                objectMetadata.setContentType(contentType);
                // 设置文件大小
                objectMetadata.setContentLength(file.getSize());
                
                // 8. 上传文件到OSS
                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        ossConfig.getBucketName(), 
                        ossFilePath, 
                        inputStream, 
                        objectMetadata
                );
                
                // 执行上传
                PutObjectResult result = ossClient.putObject(putObjectRequest);
                
                // 9. 构建访问URL
                String urlPrefix = ossConfig.getUrlPrefix();
                String fileUrl;
                if (urlPrefix != null && !urlPrefix.isEmpty()) {
                    // 如果配置了url-prefix，直接使用
                    fileUrl = urlPrefix + ossFilePath;
                } else {
                    // 如果没有配置url-prefix，动态构建
                    fileUrl = "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + ossFilePath;
                }

                // 10. 返回上传结果
                UploadVO uploadVO = new UploadVO();
                uploadVO.setUrl(fileUrl);
                return Result.success("上传成功", uploadVO);
            } catch (OSSException e) {
                // OSS上传异常处理
                return Result.error("OSS上传失败：" + e.getErrorMessage());
            }

        } catch (IOException e) {
            return Result.error("文件读取失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("服务器内部错误：" + e.getMessage());
        }
    }
}
