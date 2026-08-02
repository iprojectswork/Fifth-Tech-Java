package com.fifthtech.vo.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CacheKeyItemVO
 *
 * @description 缓存键列表项：key / type / ttl
 * @date 2026-08-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheKeyItemVO {

    private String key;

    private String type;

    /**
     * -2 不存在；-1 永不过期；&gt;=0 剩余秒
     */
    private Long ttl;
}