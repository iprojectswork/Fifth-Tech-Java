package com.fifthtech.dto.code;

import lombok.Data;

import java.io.Serializable;

/**
 * CodeRuleQueryDTO
 *
 * @author RH
 * @description 编码规则查询条件（前端入参）
 * @date 2026-08-15
 */
@Data
public class CodeRuleQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer current;

    private Integer size;

    private String ruleCode;

    private String ruleName;

    private Integer status;

    private Integer offset;

    private Integer limit;
}
