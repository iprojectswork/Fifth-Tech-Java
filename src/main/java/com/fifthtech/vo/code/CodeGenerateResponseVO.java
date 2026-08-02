package com.fifthtech.vo.code;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * CodeGenerateResponseVO
 *
 * <p>取号响应：{@code codes} 为本次生成的编号数组（升序）。</p>
 *
 * @author RH
 * @description 取号响应
 * @date 2026-08-02
 */
@Data
public class CodeGenerateResponseVO implements Serializable {

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
