package com.booktrad.book.vo;

import lombok.Data;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 图片上传结果VO，用于返回上传成功后的图片URL
 * @Date 2025/12/30
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class UploadVO {

    /**
     * 图片访问URL
     */
    private String url;
}
