package com.fifthtech.vo.code;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author RH
 * @ClassName CodeRuleVO
 * @description: 编码规则视图
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
public class CodeRuleVO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 有序片段数组
     */
    private List<CodeSegmentVO> segments;

    /**
     * 预支号段大小
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

    /**
     * 创建人 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createId;

    /**
     * 创建人姓名
     */
    private String createName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateId;

    /**
     * 更新人姓名
     */
    private String updateName;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}