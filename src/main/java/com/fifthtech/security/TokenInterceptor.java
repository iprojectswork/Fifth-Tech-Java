package com.fifthtech.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author RH
 * @ClassName TokenInterceptor
 * @description: Token拦截器
 * @date 2026年08月16日
 * @version: 1.0
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenUtils tokenUtils;

    @Value("${auth.token.header:Authorization}")
    private String header;

    @Value("${auth.token.prefix:Bearer }")
    private String prefix;

    @Value("${auth.token.white-list:}")
    private String whiteList;

    /**
    * @description: 请求拦截：白名单放行；其余校验 Bearer Token，命中后写入 UserContext
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [request, response, handler]
    * @return: boolean
    **/
    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
        String path = request.getRequestURI();

        if (isWhiteListed(path)) {
            return true;
        }

        String token = extractToken(request);
        if (token == null) {
            writeUnauthorized(response, "未登录");
            return false;
        }

        Long userId = tokenUtils.getUserIdByToken(token);
        if (userId == null) {
            writeUnauthorized(response, "Token无效或已过期");
            return false;
        }

        UserContext.setCurrentUserId(userId);
        return true;
    }

    /**
    * @description: 请求完成后清理 ThreadLocal 中的用户 ID
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [request, response, handler, ex]
    * @return: void
    **/
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    /**
    * @description: 判断请求路径是否在白名单内（startsWith 模糊匹配）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [path]
    * @return: boolean
    **/
    private boolean isWhiteListed(String path) {
        if (whiteList == null || whiteList.isEmpty()) {
            return false;
        }
        return Arrays.stream(whiteList.split(","))
                .anyMatch(whitePath -> path.startsWith(whitePath.trim()));
    }

    /**
    * @description: 从请求头按配置前缀提取 Token
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [request]
    * @return: {@link String}
    **/
    private String extractToken(HttpServletRequest request) {
        String headerValue = request.getHeader(header);
        if (headerValue != null && headerValue.startsWith(prefix)) {
            return headerValue.substring(prefix.length());
        }
        return null;
    }

    /**
    * @description: 直接向响应写入 401 JSON
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [response, message]
    * @return: void
    **/
    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\",\"data\":null}");
    }
}