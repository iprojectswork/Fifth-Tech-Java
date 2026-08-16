package com.fifthtech.dto.cache;

import lombok.Data;

/**
 * @author RH
 * @ClassName CacheExpireDTO
 * @description: 缓存 TTL 修改请求
 * @date 2026年08月01日
 * @version: 1.0
 */
@Data
public class CacheExpireDTO {

    /**
     * 缓存键
     */
    private String key;

    /**
     * TTL 秒数（-1 表示 PERSIST）
     */
    private Long ttlSeconds;
}