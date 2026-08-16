package com.fifthtech.vo.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author RH
 * @ClassName CacheKeyItemVO
 * @description: 缓存键列表项
 * @date 2026年08月01日
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheKeyItemVO {

    /**
     * 缓存键
     */
    private String key;

    /**
     * 值类型
     */
    private String type;

    /**
     * TTL（-2 不存在；-1 永不过期；&gt;=0 剩余秒）
     */
    private Long ttl;
}