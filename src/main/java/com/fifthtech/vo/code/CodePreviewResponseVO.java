package com.fifthtech.vo.code;

import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName CodePreviewResponseVO
 * @description: 试拼响应
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
public class CodePreviewResponseVO implements Serializable {

    /**
     * 序列化版本号
     */
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