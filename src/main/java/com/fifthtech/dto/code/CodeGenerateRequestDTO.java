package com.fifthtech.dto.code;

import lombok.Data;

import java.io.Serializable;

/**
 * CodeGenerateRequestDTO
 *
 * <p>取号 API 入参。{@code count} 缺省 1、上限 {@code code.generate.max-batch-count}。
 * {@code bizTime} 可选 ISO-8601 字符串；为空则取系统当前时间（默认时区
 * {@code code.timezone}）。仅走登录 Token 拦截，无业务权限码。</p>
 *
 * @author RH
 * @description 取号请求
 * @date 2026-08-02
 */
@Data
public class CodeGenerateRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规则编码（必填）
     */
    private String ruleCode;

    /**
     * 本次取多少个号（默认 1）
     */
    private Integer count;

    /**
     * 业务时间（可选，ISO-8601 如 2026-08-02T15:30:00Z）
     */
    private String bizTime;
}
