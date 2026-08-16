package com.fifthtech.dto.code;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName CodeSequenceQueryDTO
 * @description: 流水水位查询条件
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
public class CodeSequenceQueryDTO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 规则 ID（与 ruleCode 二选一；同时存在以 ruleId 为准）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ruleId;

    /**
     * 规则编码
     */
    private String ruleCode;
}