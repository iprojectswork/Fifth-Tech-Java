package com.fifthtech.config;

import com.fifthtech.security.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author RH
 * @ClassName WebConfig
 * @description: Web配置
 * @date 2026年08月16日
 * @version: 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    /**
    * @description: 注册 Token 拦截器到 /**，排除 /error
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [registry]
    * @return: void
    **/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }
}