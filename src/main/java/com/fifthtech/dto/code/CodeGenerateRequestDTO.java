package com.fifthtech.dto.code;

import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName CodeGenerateRequestDTO
 * @description: 取号请求
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
public class CodeGenerateRequestDTO implements Serializable {

    /**
     * 序列化版本号
     */
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
     * 业务时间（可选，ISO-8601）
     */
    private String bizTime;
}