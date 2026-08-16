package com.fifthtech.dto.code;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author RH
 * @ClassName CodeRuleDTO
 * @description: 编码规则数据传输对象
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
public class CodeRuleDTO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键（edit 时必填）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 规则编码（全局唯一）
     */
    private String ruleCode;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 有序片段数组
     */
    private List<CodeSegmentDTO> segments;

    /**
     * 预支号段大小（1~5000）
     */
    private Integer batchSize;

    /**
     * 状态（1：启用 0：禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}