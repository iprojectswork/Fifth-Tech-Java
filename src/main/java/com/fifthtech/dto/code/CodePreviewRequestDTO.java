package com.fifthtech.dto.code;

import lombok.Data;

import java.io.Serializable;

/**
 * CodePreviewRequestDTO
 *
 * <p>试拼请求（不消费号）。</p>
 *
 * @author RH
 * @description 试拼请求
 * @date 2026-08-02
 */
@Data
public class CodePreviewRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规则编码（必填）
     */
    private String ruleCode;

    /**
     * 业务时间（可选，ISO-8601；为空取当前时间）
     */
    private String bizTime;
}
