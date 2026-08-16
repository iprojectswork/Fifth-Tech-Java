package com.fifthtech.service.cache.impl;

import com.fifthtech.dto.cache.CacheExpireDTO;
import com.fifthtech.dto.cache.CacheSetDTO;
import com.fifthtech.service.cache.CacheService;
import com.fifthtech.vo.cache.CacheDetailVO;
import com.fifthtech.vo.cache.CacheKeyItemVO;
import com.fifthtech.vo.cache.CachePageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author RH
 * @ClassName CacheServiceImpl
 * @description: 缓存服务实现
 * @date 2026年08月01日
 * @version: 1.0
 */
@Service
public class CacheServiceImpl implements CacheService {

    private static final String USER_TOKEN_KEY_PREFIX = "auth:user:";
    private static final String USER_TOKEN_KEY_SUFFIX = ":token";
    private static final String TOKEN_KEY_PREFIX = "auth:token:";

    private static final String TYPE_NONE = "none";
    private static final String TYPE_STRING = "string";

    private static final long SCAN_COUNT_HINT = 500L;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${cache.admin.max-scan-keys:5000}")
    private int maxScanKeys;

    @Value("${cache.admin.max-value-bytes:262144}")
    private int maxValueBytes;

    @Override
    public int getMaxValueBytes() {
        return maxValueBytes;
    }

    @Override
    public CachePageVO listKeys(String pattern, Integer current, Integer size) {
        String match = (pattern == null || pattern.isEmpty()) ? "*" : pattern;
        int pageNum = (current == null || current < 1) ? 1 : current;
        int pageSize = (size == null || size < 1) ? 10 : size;

        List<String> collected = new ArrayList<>(Math.min(maxScanKeys, 1024));
        boolean truncated = false;

        ScanOptions options = ScanOptions.scanOptions().match(match).count(SCAN_COUNT_HINT).build();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                collected.add(cursor.next());
                if (collected.size() >= maxScanKeys) {
                    truncated = true;
                    break;
                }
            }
        }

        Collections.sort(collected);
        int total = collected.size();

        int from = Math.min((pageNum - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);
        List<CacheKeyItemVO> records = new ArrayList<>(to - from);
        for (int i = from; i < to; i++) {
            String key = collected.get(i);
            records.add(new CacheKeyItemVO(key, typeOf(key), ttlOf(key)));
        }

        CachePageVO page = new CachePageVO();
        page.setRecords(records);
        page.setTotal(total);
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page.setTruncated(truncated);
        page.setMax(maxScanKeys);
        return page;
    }

    @Override
    public CacheDetailVO getDetail(String key) {
        if (!isValidKey(key)) {
            return null;
        }
        DataType type = redisTemplate.type(key);
        if (type == null || type == DataType.NONE) {
            return null;
        }
        if (type != DataType.STRING) {
            return null;
        }
        String value = redisTemplate.opsForValue().get(key);
        if (value != null && value.getBytes(StandardCharsets.UTF_8).length > maxValueBytes) {
            return null;
        }
        CacheDetailVO vo = new CacheDetailVO();
        vo.setKey(key);
        vo.setType(type.code());
        vo.setTtl(ttlOf(key));
        vo.setValue(value);
        return vo;
    }

    @Override
    public String getType(String key) {
        if (!isValidKey(key)) {
            return TYPE_NONE;
        }
        return typeOf(key);
    }

    @Override
    public boolean set(CacheSetDTO dto) {
        if (dto == null) {
            return false;
        }
        String key = dto.getKey();
        String value = dto.getValue();
        Long ttl = dto.getTtlSeconds();
        if (!isValidKey(key)) {
            return false;
        }
        if (value == null) {
            return false;
        }
        if (value.getBytes(StandardCharsets.UTF_8).length > maxValueBytes) {
            return false;
        }
        DataType existingType = redisTemplate.type(key);
        if (existingType != null && existingType != DataType.NONE && existingType != DataType.STRING) {
            return false;
        }
        if (ttl == null || ttl <= 0) {
            redisTemplate.opsForValue().set(key, value);
        } else {
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        }
        return true;
    }

    @Override
    public boolean expire(CacheExpireDTO dto) {
        if (dto == null) {
            return false;
        }
        String key = dto.getKey();
        Long ttl = dto.getTtlSeconds();
        if (!isValidKey(key) || ttl == null) {
            return false;
        }
        DataType type = redisTemplate.type(key);
        if (type != DataType.STRING) {
            return false;
        }
        if (ttl < 0) {
            return Boolean.TRUE.equals(redisTemplate.persist(key));
        }
        return Boolean.TRUE.equals(redisTemplate.expire(key, ttl, TimeUnit.SECONDS));
    }

    @Override
    public long delete(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0L;
        }
        long count = 0L;
        for (String key : keys) {
            if (!isValidKey(key)) {
                continue;
            }
            if (isUserTokenKey(key)) {
                count += deleteUserTokenPair(key);
            } else if (key.startsWith(TOKEN_KEY_PREFIX)) {
                count += deleteTokenPair(key);
            } else {
                count += deletePlain(key);
            }
        }
        return count;
    }

    private long deleteUserTokenPair(String userKey) {
        String token = redisTemplate.opsForValue().get(userKey);
        long n = Boolean.TRUE.equals(redisTemplate.delete(userKey)) ? 1L : 0L;
        if (token == null || token.isEmpty()) {
            return n;
        }
        if (Boolean.TRUE.equals(redisTemplate.delete(TOKEN_KEY_PREFIX + token))) {
            n++;
        }
        return n;
    }

    private long deleteTokenPair(String tokenKey) {
        String userId = redisTemplate.opsForValue().get(tokenKey);
        long n = Boolean.TRUE.equals(redisTemplate.delete(tokenKey)) ? 1L : 0L;
        if (userId == null || userId.isEmpty()) {
            return n;
        }
        try {
            Long.parseLong(userId);
        } catch (NumberFormatException ex) {
            return n;
        }
        if (Boolean.TRUE.equals(redisTemplate.delete(USER_TOKEN_KEY_PREFIX + userId + USER_TOKEN_KEY_SUFFIX))) {
            n++;
        }
        return n;
    }

    private long deletePlain(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key)) ? 1L : 0L;
    }

    private boolean isUserTokenKey(String key) {
        return key.startsWith(USER_TOKEN_KEY_PREFIX) && key.endsWith(USER_TOKEN_KEY_SUFFIX);
    }

    private String typeOf(String key) {
        DataType type = redisTemplate.type(key);
        if (type == null || type == DataType.NONE) {
            return TYPE_NONE;
        }
        return type.code();
    }

    private long ttlOf(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl == null ? -2L : ttl;
    }

    private static boolean isValidKey(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }
        for (int i = 0; i < key.length(); i++) {
            if (Character.isWhitespace(key.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}