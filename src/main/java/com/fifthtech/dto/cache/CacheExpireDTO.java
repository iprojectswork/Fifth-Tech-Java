package com.fifthtech.dto.cache;

import lombok.Data;

/**
 * CacheExpireDTO
 *
 * @description 仅修改 TTL；ttlSeconds = -1 表示 PERSIST
 * @date 2026-08-01
 */
@Data
public class CacheExpireDTO {

    private String key;

    private Long ttlSeconds;
}