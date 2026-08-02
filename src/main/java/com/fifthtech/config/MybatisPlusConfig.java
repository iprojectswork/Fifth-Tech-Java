package com.fifthtech.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置
 *
 * @description 注册分页插件，使 Page#getTotal 返回真实总条数（否则 total 恒为 0）
 * @date 2026-08-02
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        // 溢出总页后回到首页，避免前端翻页到空页
        pagination.setOverflow(true);
        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}
