package com.fifthtech.service.cache;

import com.fifthtech.dto.cache.CacheExpireDTO;
import com.fifthtech.dto.cache.CacheSetDTO;
import com.fifthtech.vo.cache.CacheDetailVO;
import com.fifthtech.vo.cache.CachePageVO;

import java.util.List;

/**
 * CacheService
 *
 * @description Redis String 键管理服务（SCAN 列表 / 详情 / 写入 / TTL / 删除 + auth 对称清理）
 * @date 2026-08-01
 */
public interface CacheService {

    /**
     * SCAN 收集 key，按字典序排序后内存分页；命中 max-scan-keys 上限返回 truncated=true
     */
    CachePageVO listKeys(String pattern, Integer current, Integer size);

    /**
     * 详情；非 String 返回 null（由 Controller 转译为业务错误）
     */
    CacheDetailVO getDetail(String key);

    /**
     * 仅返回键类型（Redis type 命令结果的小写编码）；key 不存在返回 "none"
     */
    String getType(String key);

    /**
     * 新增/覆盖 String 键；ttlSeconds=null 不设置过期；&gt;0 setEx
     *
     * @return true 写入成功
     */
    boolean set(CacheSetDTO dto);

    /**
     * 仅修改 TTL；ttlSeconds&gt;0 expire；-1 PERSIST；非 String 返回 false
     */
    boolean expire(CacheExpireDTO dto);

    /**
     * 批量删除（auth:* 对称清理）；空列表返回 0
     *
     * @return 实际删除 key 数（含对称键）
     */
    long delete(List<String> keys);

    /**
     * 单个 value 的 UTF-8 字节上限（Controller 用于在 set 前做 oversize 预校验，避免与 service 内部阈值漂移）
     */
    int getMaxValueBytes();
}