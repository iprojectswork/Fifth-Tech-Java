package com.fifthtech.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
