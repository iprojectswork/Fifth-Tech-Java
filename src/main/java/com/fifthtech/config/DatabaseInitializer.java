package com.fifthtech.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author RH
 * @ClassName DatabaseInitializer
 * @description: 数据库初始化
 * @date 2026年08月16日
 * @version: 1.0
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    /**
    * @description: 注入 JdbcTemplate，用于执行启动初始化 SQL
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [jdbcTemplate]
    * @return: 
    **/
    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
    * @description: 启动钩子；当前为占位（自动建表已注释，由手工执行 SQL 完成）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [args]
    * @return: void
    **/
    @Override
    public void run(String... args) throws Exception {
//        try {
//            ClassPathResource resource = new ClassPathResource("sql/user.sql");
//            String sql = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//            jdbcTemplate.execute(sql);
//        } catch (Exception e) {
//            System.out.println("数据库初始化失败或已存在: " + e.getMessage());
//        }
    }
}