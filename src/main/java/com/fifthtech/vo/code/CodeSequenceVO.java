package com.fifthtech.vo.code;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName CodeSequenceVO
 * @description: 流水水位视图
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
public class CodeSequenceVO implements Serializable {

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
     * 规则 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ruleId;

    /**
     * 周期键
     */
    private String periodKey;

    /**
     * 已预支到的最大序号（含）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long currentMax;
}