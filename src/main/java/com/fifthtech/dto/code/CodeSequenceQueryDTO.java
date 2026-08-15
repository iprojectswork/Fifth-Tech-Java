package com.fifthtech.dto.code;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * CodeSequenceQueryDTO
 *
 * <p>水位查询入参：{@code ruleId} / {@code ruleCode} 至少一项。</p>
 *
 * @author RH
 * @description 流水水位查询条件（前端入参）
 * @date 2026-08-02
 */
@Data
public class CodeSequenceQueryDTO implements Serializable {

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
