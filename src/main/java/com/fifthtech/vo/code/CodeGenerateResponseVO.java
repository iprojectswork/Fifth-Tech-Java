package com.fifthtech.vo.code;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author RH
 * @ClassName CodeGenerateResponseVO
 * @description: 取号响应
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
public class CodeGenerateResponseVO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 规则编码（回显）
     */
    private String ruleCode;

    /**
     * 本次返回的编码列表
     */
    private List<String> codes;
}