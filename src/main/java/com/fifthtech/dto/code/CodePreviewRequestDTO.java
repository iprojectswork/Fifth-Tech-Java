package com.fifthtech.dto.code;

import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName CodePreviewRequestDTO
 * @description: 试拼请求
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
public class CodePreviewRequestDTO implements Serializable {

    /**
     * 序列化版本号
     */
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