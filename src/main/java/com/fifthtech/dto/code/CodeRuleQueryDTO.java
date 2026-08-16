package com.fifthtech.dto.code;

import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName CodeRuleQueryDTO
 * @description: 编码规则查询条件
 * @date 2026年08月15日
 * @version: 1.0
 */
@Data
public class CodeRuleQueryDTO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private Integer current;

    /**
     * 每页条数
     */
    private Integer size;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 状态（1：启用 0：禁用）
     */
    private Integer status;

    /**
     * 偏移量
     */
    private Integer offset;

    /**
     * 取多少条
     */
    private Integer limit;
}