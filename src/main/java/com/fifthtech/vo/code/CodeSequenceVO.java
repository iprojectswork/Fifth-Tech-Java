package com.fifthtech.vo.code;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * CodeSequenceVO
 *
 * <p>流水水位视图。雪花 id 序列化为字符串，避免 JS Number 精度丢失。</p>
 *
 * @author RH
 * @description 流水水位视图
 * @date 2026-08-02
 */
@Data
public class CodeSequenceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String ruleCode;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ruleId;
    private String periodKey;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long currentMax;
}
