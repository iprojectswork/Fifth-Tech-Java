package com.fifthtech.dto.cache;

import lombok.Data;

/**
 * @author RH
 * @ClassName CacheSetDTO
 * @description: 缓存写入请求
 * @date 2026年08月01日
 * @version: 1.0
 */
@Data
public class CacheSetDTO {

    /**
     * 缓存键
     */
    private String key;

    /**
     * 缓存值（String 类型）
     */
    private String value;

    /**
     * TTL 秒数（null 不设置过期；&gt;0 为过期秒数）
     */
    private Long ttlSeconds;
}