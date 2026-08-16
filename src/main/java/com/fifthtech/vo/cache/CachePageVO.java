package com.fifthtech.vo.cache;

import lombok.Data;

import java.util.List;

/**
 * @author RH
 * @ClassName CachePageVO
 * @description: 缓存键分页结果
 * @date 2026年08月01日
 * @version: 1.0
 */
@Data
public class CachePageVO {

    /**
     * 当前页键列表
     */
    private List<CacheKeyItemVO> records;

    /**
     * 当前返回条数
     */
    private long total;

    /**
     * 当前页
     */
    private int current;

    /**
     * 每页条数
     */
    private int size;

    /**
     * 是否命中 max-scan-keys 上限
     */
    private boolean truncated;

    /**
     * 扫描上限
     */
    private int max;
}