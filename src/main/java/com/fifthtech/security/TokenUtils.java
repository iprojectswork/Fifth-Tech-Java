package com.fifthtech.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author RH
 * @ClassName TokenUtils
 * @description: Token工具
 * @date 2026年08月16日
 * @version: 1.0
 */
@Component
public class TokenUtils {

    private static final String TOKEN_KEY_PREFIX = "auth:token:";
    private static final String USER_TOKEN_KEY_PREFIX = "auth:user:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${auth.token.expiration:86400}")
    private Long expiration;

    /**
    * @description: 为用户生成 Token 并写入 Redis（双向键：token→userId 与 userId→token）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: {@link String}
    **/
    public String generateToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String tokenKey = TOKEN_KEY_PREFIX + token;
        String userTokenKey = USER_TOKEN_KEY_PREFIX + userId + ":token";

        redisTemplate.opsForValue().set(tokenKey, String.valueOf(userId), expiration, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(userTokenKey, token, expiration, TimeUnit.SECONDS);

        return token;
    }

    /**
    * @description: 根据 Token 解析用户 ID
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [token]
    * @return: {@link Long}
    **/
    public Long getUserIdByToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        String userId = redisTemplate.opsForValue().get(tokenKey);
        return userId != null ? Long.parseLong(userId) : null;
    }

    /**
    * @description: 按 Token 清除（同步删 token→userId 与 userId→token 双向键）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [token]
    * @return: void
    **/
    public void removeToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        String userId = redisTemplate.opsForValue().get(tokenKey);

        if (userId != null) {
            String userTokenKey = USER_TOKEN_KEY_PREFIX + userId + ":token";
            redisTemplate.delete(tokenKey);
            redisTemplate.delete(userTokenKey);
        }
    }

    /**
    * @description: 按用户 ID 强制下线（清该用户的双向 Token 键）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: void
    **/
    public void removeTokenByUserId(Long userId) {
        String userTokenKey = USER_TOKEN_KEY_PREFIX + userId + ":token";
        String token = redisTemplate.opsForValue().get(userTokenKey);

        if (token != null) {
            String tokenKey = TOKEN_KEY_PREFIX + token;
            redisTemplate.delete(tokenKey);
            redisTemplate.delete(userTokenKey);
        }
    }

    /**
    * @description: 校验 Token 是否有效（Redis 中存在即视为有效）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [token]
    * @return: boolean
    **/
    public boolean validateToken(String token) {
        return getUserIdByToken(token) != null;
    }
}