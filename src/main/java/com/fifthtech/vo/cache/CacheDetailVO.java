package com.fifthtech.vo.cache;

import lombok.Data;

/**
 * @author RH
 * @ClassName CacheDetailVO
 * @description: 缓存键详情视图
 * @date 2026年08月01日
 * @version: 1.0
 */
@Data
public class CacheDetailVO {

    /**
     * 缓存键
     */
    private String key;

    /**
     * 值类型（仅 String）
     */
    private String type;

    /**
     * TTL（秒；-1 永不过期；-2 不存在）
     */
    private Long ttl;

    /**
     * 缓存值（非 String 类型不返回）
     */
    private String value;
}