package com.fifthtech.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class TokenUtils {

    private static final String TOKEN_KEY_PREFIX = "auth:token:";
    private static final String USER_TOKEN_KEY_PREFIX = "auth:user:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${auth.token.expiration:86400}")
    private Long expiration;

    public String generateToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String tokenKey = TOKEN_KEY_PREFIX + token;
        String userTokenKey = USER_TOKEN_KEY_PREFIX + userId + ":token";

        redisTemplate.opsForValue().set(tokenKey, String.valueOf(userId), expiration, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(userTokenKey, token, expiration, TimeUnit.SECONDS);

        return token;
    }

    public Long getUserIdByToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        String userId = redisTemplate.opsForValue().get(tokenKey);
        return userId != null ? Long.parseLong(userId) : null;
    }

    public void removeToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        String userId = redisTemplate.opsForValue().get(tokenKey);

        if (userId != null) {
            String userTokenKey = USER_TOKEN_KEY_PREFIX + userId + ":token";
            redisTemplate.delete(tokenKey);
            redisTemplate.delete(userTokenKey);
        }
    }

    public void removeTokenByUserId(Long userId) {
        String userTokenKey = USER_TOKEN_KEY_PREFIX + userId + ":token";
        String token = redisTemplate.opsForValue().get(userTokenKey);

        if (token != null) {
            String tokenKey = TOKEN_KEY_PREFIX + token;
            redisTemplate.delete(tokenKey);
            redisTemplate.delete(userTokenKey);
        }
    }

    public boolean validateToken(String token) {
        return getUserIdByToken(token) != null;
    }
}
