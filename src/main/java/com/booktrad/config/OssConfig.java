package com.booktrad.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置类
 * 用于初始化OSS客户端，读取配置文件中的OSS参数
 */
@Configuration
public class OssConfig {

    /**
     * OSS服务端点
     */
    @Value("${oss.endpoint}")
    private String endpoint;

    /**
     * 阿里云访问密钥ID
     */
    @Value("${oss.access-key-id}")
    private String accessKeyId;

    /**
     * 阿里云访问密钥Secret
     */
    @Value("${oss.access-key-secret}")
    private String accessKeySecret;

    /**
     * OSS存储空间名称
     */
    @Value("${oss.bucket-name}")
    private String bucketName;

    /**
     * OSS存储空间所在区域
     */
    @Value("${oss.region}")
    private String region;

    /**
     * 上传文件的前缀路径
     */
    @Value("${oss.prefix:}")
    private String prefix;

    /**
     * 访问URL前缀
     */
    @Value("${oss.url-prefix:}")
    private String urlPrefix;

    /**
     * 初始化OSS客户端
     * @return OSS客户端实例
     */
    @Bean
    public OSS ossClient() {
        // 使用OSSClientBuilder创建OSS客户端
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    // getter方法，供其他组件使用
    public String getEndpoint() {
        return endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getRegion() {
        return region;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }
}