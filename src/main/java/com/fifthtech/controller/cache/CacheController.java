package com.fifthtech.controller.cache;

import com.fifthtech.common.Result;
import com.fifthtech.dto.cache.CacheDeleteDTO;
import com.fifthtech.dto.cache.CacheExpireDTO;
import com.fifthtech.dto.cache.CacheSetDTO;
import com.fifthtech.service.cache.CacheService;
import com.fifthtech.vo.cache.CacheDetailVO;
import com.fifthtech.vo.cache.CachePageVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CacheController
 *
 * @description Redis String 键管理接口（列表 / 详情 / 写入 / TTL / 删除）
 * @date 2026-08-01
 */
@RestController
@RequestMapping("/cache")
public class CacheController {

    private static final String TYPE_NONE = "none";
    private static final String TYPE_STRING = "string";

    @Resource
    private CacheService cacheService;

    /**
     * 分页列表（SCAN + 内存分页）
     */
    @GetMapping("/keys")
    public Result<CachePageVO> keys(
            @RequestParam(required = false, defaultValue = "*") String pattern,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        CachePageVO page = cacheService.listKeys(pattern, current, size);
        return Result.success("查询成功", page);
    }

    /**
     * 详情；区分 键不存在 / 仅支持 String 类型 / value 超过大小上限
     */
    @GetMapping("/detail")
    public Result<CacheDetailVO> detail(@RequestParam String key) {
        if (!isValidKey(key)) {
            return Result.error("key 不能为空");
        }
        String type = cacheService.getType(key);
        if (TYPE_NONE.equalsIgnoreCase(type)) {
            return Result.error("键不存在");
        }
        if (!TYPE_STRING.equalsIgnoreCase(type)) {
            return Result.error("仅支持 String 类型");
        }
        CacheDetailVO vo = cacheService.getDetail(key);
        if (vo == null) {
            return Result.error("value 超过大小上限");
        }
        return Result.success("查询成功", vo);
    }

    /**
     * 写入 / 覆盖 String；null ttl = 不设置过期；&gt;0 = setEx 秒数
     */
    @PostMapping
    public Result<Void> set(@RequestBody CacheSetDTO dto) {
        if (dto == null) {
            return Result.error("请求体不能为空");
        }
        String key = dto.getKey();
        String value = dto.getValue();
        if (!isValidKey(key)) {
            return Result.error("key 不能为空");
        }
        if (value == null) {
            return Result.error("value 不能为 null");
        }
        if (value.getBytes(StandardCharsets.UTF_8).length > cacheService.getMaxValueBytes()) {
            return Result.error("value 超过大小上限");
        }
        String type = cacheService.getType(key);
        if (!TYPE_NONE.equalsIgnoreCase(type) && !TYPE_STRING.equalsIgnoreCase(type)) {
            return Result.error("仅支持 String 类型");
        }
        boolean ok = cacheService.set(dto);
        if (ok) {
            return Result.success("保存成功", null);
        }
        return Result.error("value 超过大小上限");
    }

    /**
     * 仅改 TTL；-1 = PERSIST；非 String 拒绝
     */
    @PutMapping("/expire")
    public Result<Void> expire(@RequestBody CacheExpireDTO dto) {
        if (dto == null) {
            return Result.error("请求体不能为空");
        }
        String key = dto.getKey();
        Long ttl = dto.getTtlSeconds();
        if (!isValidKey(key)) {
            return Result.error("key 不能为空");
        }
        if (ttl == null) {
            return Result.error("ttlSeconds 不能为空");
        }
        String type = cacheService.getType(key);
        if (TYPE_NONE.equalsIgnoreCase(type)) {
            return Result.error("键不存在");
        }
        if (!TYPE_STRING.equalsIgnoreCase(type)) {
            return Result.error("仅支持 String 类型");
        }
        boolean ok = cacheService.expire(dto);
        if (ok) {
            return Result.success("TTL 更新成功", null);
        }
        return Result.error("TTL 更新失败");
    }

    /**
     * 批量删除；空列表拒绝；auth:* 对称清理；blank/whitespace key 直接跳过
     */
    @DeleteMapping
    public Result<Map<String, Object>> delete(@RequestBody CacheDeleteDTO dto) {
        if (dto == null) {
            return Result.error("请求体不能为空");
        }
        List<String> rawKeys = dto.getKeys();
        if (rawKeys == null || rawKeys.isEmpty()) {
            return Result.error("keys 不能为空");
        }
        List<String> validKeys = new ArrayList<>(rawKeys.size());
        for (String cacheKey : rawKeys) {
            if (isValidKey(cacheKey)) {
                validKeys.add(cacheKey);
            }
        }
        if (validKeys.isEmpty()) {
            return Result.error("keys 不能为空");
        }
        long count = cacheService.delete(validKeys);
        Map<String, Object> data = new HashMap<>(2);
        data.put("deleted", count);
        data.put("requested", validKeys.size());
        return Result.success("删除成功", data);
    }

    private static boolean isValidKey(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }
        for (int i = 0; i < key.length(); i++) {
            if (Character.isWhitespace(key.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}