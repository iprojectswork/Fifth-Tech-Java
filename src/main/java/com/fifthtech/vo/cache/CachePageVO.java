package com.fifthtech.vo.cache;

import lombok.Data;

import java.util.List;

/**
 * CachePageVO
 *
 * @description 缓存键分页结果；truncated 表示命中 max-scan-keys 上限
 * @date 2026-08-01
 */
@Data
public class CachePageVO {

    private List<CacheKeyItemVO> records;

    private long total;

    private int current;

    private int size;

    private boolean truncated;

    private int max;
}