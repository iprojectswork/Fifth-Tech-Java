package com.fifthtech.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author RH
 * @ClassName MybatisPlusConfig
 * @description: MyBatis-Plus配置
 * @date 2026年08月02日
 * @version: 1.0
 */
@Configuration
public class MybatisPlusConfig {

    /**
    * @description: 注册分页插件（PostgreSQL 方言），开启溢出回到首页，保证 Page#getTotal 返回真实总条数
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link MybatisPlusInterceptor}
    **/
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