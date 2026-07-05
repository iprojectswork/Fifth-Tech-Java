package com.fifthtech.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

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

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean isWhiteListed(String path) {
        if (whiteList == null || whiteList.isEmpty()) {
            return false;
        }
        return Arrays.stream(whiteList.split(","))
                .anyMatch(whitePath -> path.startsWith(whitePath.trim()));
    }

    private String extractToken(HttpServletRequest request) {
        String headerValue = request.getHeader(header);
        if (headerValue != null && headerValue.startsWith(prefix)) {
            return headerValue.substring(prefix.length());
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\",\"data\":null}");
    }
}
