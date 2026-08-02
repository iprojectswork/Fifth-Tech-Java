package com.fifthtech.vo.cache;

import lombok.Data;

/**
 * CacheDetailVO
 *
 * @description 缓存键详情：key / type / ttl / value（非 String 不返回 value）
 * @date 2026-08-01
 */
@Data
public class CacheDetailVO {

    private String key;

    private String type;

    private Long ttl;

    private String value;
}