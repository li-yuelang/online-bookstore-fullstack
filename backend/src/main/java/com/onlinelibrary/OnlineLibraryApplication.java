package com.onlinelibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 在线书店系统 - 主启动类
 * 
 * 基于 Spring Boot 2.7 构建，提供 RESTful API 支持。
 * 包含用户管理、书籍浏览、购物车、订单管理和数据统计等功能。
 */
@SpringBootApplication
public class OnlineLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineLibraryApplication.class, args);
    }
}