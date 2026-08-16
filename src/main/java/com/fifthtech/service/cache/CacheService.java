package com.fifthtech.service.cache;

import com.fifthtech.dto.cache.CacheExpireDTO;
import com.fifthtech.dto.cache.CacheSetDTO;
import com.fifthtech.vo.cache.CacheDetailVO;
import com.fifthtech.vo.cache.CachePageVO;

import java.util.List;

/**
 * @author RH
 * @ClassName CacheService
 * @description: Redis String 键管理服务接口
 * @date 2026年08月01日
 * @version: 1.0
 */
public interface CacheService {

    /**
    * @description: SCAN 收集键并按字典序排序，内存分页返回；命中 max-scan-keys 上限标记 truncated=true
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [pattern, current, size]
    * @return: {@link CachePageVO}
    **/
    CachePageVO listKeys(String pattern, Integer current, Integer size);

    /**
    * @description: 根据key查询缓存详情
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [key]
    * @return: {@link CacheDetailVO}
    **/
    CacheDetailVO getDetail(String key);

    /**
    * @description: 根据key查询缓存类型
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [key]
    * @return: {@link String}
    **/
    String getType(String key);

    /**
    * @description: 新增或覆盖 String 键，ttlSeconds 为空不设置过期，大于 0 走 SETEX；非 String 类型与超长 value 拒绝
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: boolean
    **/
    boolean set(CacheSetDTO dto);

    /**
    * @description: 修改 String 键的 TTL，大于 0 走 EXPIRE，-1 走 PERSIST；非 String 类型拒绝
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: boolean
    **/
    boolean expire(CacheExpireDTO dto);

    /**
    * @description: 根据key批量删除缓存，Token键对称清理
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [keys]
    * @return: long
    **/
    long delete(List<String> keys);

    /**
    * @description: 返回单 value 的 UTF-8 字节上限，Controller 用于写入前预校验
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: int
    **/
    int getMaxValueBytes();
}
