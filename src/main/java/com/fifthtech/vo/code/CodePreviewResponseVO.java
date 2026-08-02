package com.fifthtech.vo.code;

import lombok.Data;

import java.io.Serializable;

/**
 * CodePreviewResponseVO
 *
 * <p>试拼响应：{@code sample} 是不消费号的「假设下次取号会长什么样」（基于当前 DB 水位 +1）。</p>
 *
 * @author RH
 * @description 试拼响应
 * @date 2026-08-02
 */
@Data
public class CodePreviewResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规则编码（回显）
     */
    private String ruleCode;

    /**
     * 试拼样例
     */
    private String sample;
}
