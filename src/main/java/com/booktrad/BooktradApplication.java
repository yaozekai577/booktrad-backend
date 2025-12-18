package com.booktrad;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/** 
 * 项目名称：booktrad 
 * 版本：V1.0 
 * 
 * @Author yaozekai 
 * @Email 2321593248@qq.com 
 * @Description  项目启动类，配置MyBatis Plus分页插件，扫描所有mapper接口
 * @Date 2025-12-18 21:00:00 
 * Copyrigt (C) 2025-2026 All Right Reserved 
 * 注意：本内容为个人毕设 
 */
@SpringBootApplication
@MapperScan("com.booktrad.**.mapper")
// 扫描所有mapper接口
public class BooktradApplication {

    public static void main(String[] args) {
        SpringApplication.run(BooktradApplication.class, args);
    }

    /**
     * 配置MyBatis Plus分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件，指定数据库类型为MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
