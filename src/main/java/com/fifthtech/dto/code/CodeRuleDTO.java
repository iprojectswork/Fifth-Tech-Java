package com.fifthtech.dto.code;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * CodeRuleDTO
 *
 * <p>编码规则入参：{@code segments} 为有序片段数组（FIXED / DATE / SEQUENCE），与
 * {@code CodeRuleServiceImpl} 的校验逻辑一一对应。
 * id 以字符串收发，避免浏览器 JSON 雪花精度丢失。</p>
 *
 * @author RH
 * @description 编码规则数据传输对象
 * @date 2026-08-02
 */
@Data
public class CodeRuleDTO implements Serializable {

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
     * 有序片段数组（至少 1 个；必须含恰好 1 个 SEQUENCE、至多 1 个 DATE）
     */
    private List<CodeSegmentDTO> segments;

    /**
     * 预支号段大小（1~5000；为空时取 code.generate.default-pool-batch-size）
     */
    private Integer batchSize;

    /**
     * 状态（1 启用 / 0 禁用；默认 1）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
