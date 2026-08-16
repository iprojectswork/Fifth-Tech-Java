package com.fifthtech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author RH
 * @ClassName CorsConfig
 * @description: 跨域配置
 * @date 2026年08月16日
 * @version: 1.0
 */
@Configuration
public class CorsConfig {

    /**
    * @description: 注册全局 CORS 过滤器：所有来源、方法、Header 放行并允许凭证
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link CorsFilter}
    **/
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}