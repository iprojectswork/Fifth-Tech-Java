package com.fifthtech.dto.cache;

import lombok.Data;

import java.util.List;

/**
 * CacheDeleteDTO
 *
 * @description 批量删除请求体
 * @date 2026-08-01
 */
@Data
public class CacheDeleteDTO {

    private List<String> keys;
}