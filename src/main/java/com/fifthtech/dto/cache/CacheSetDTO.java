package com.fifthtech.dto.cache;

import lombok.Data;

/**
 * CacheSetDTO
 *
 * @description 缓存写入 / 覆盖 String 键
 * @date 2026-08-01
 */
@Data
public class CacheSetDTO {

    private String key;

    private String value;

    /**
     * 可选：秒。null = 不设置过期；&gt;0 = setEx 秒数；仅 set 接口接收，expire 接口独立处理
     */
    private Long ttlSeconds;
}