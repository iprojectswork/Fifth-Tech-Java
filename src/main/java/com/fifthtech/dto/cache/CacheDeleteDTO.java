package com.fifthtech.dto.cache;

import lombok.Data;

import java.util.List;

/**
 * @author RH
 * @ClassName CacheDeleteDTO
 * @description: 批量删除请求体
 * @date 2026年08月01日
 * @version: 1.0
 */
@Data
public class CacheDeleteDTO {

    /**
     * 要删除的键列表
     */
    private List<String> keys;
}