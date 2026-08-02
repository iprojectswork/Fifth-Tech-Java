package com.fifthtech.dto.code;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * CodeSegmentDTO
 *
 * <p>统一的片段 DTO；按 {@code type} 区分：</p>
 * <ul>
 *   <li>FIXED：{@code value} 必填</li>
 *   <li>DATE：{@code pattern} 必填，且必须在白名单（yy / yyyy / yyMM / yyyyMM / yyMMdd / yyyyMMdd）</li>
 *   <li>SEQUENCE：{@code length} 必填；{@code start} 默认 1、{@code step} 默认 1</li>
 * </ul>
 *
 * <p>{@code sortNo} 仅用于前端拖拽排序；同条规则内允许重号，最终顺序以前端提交顺序为准（一期不严格校验）。</p>
 *
 * @author RH
 * @description 编码片段定义
 * @date 2026-08-02
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeSegmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 片段类型（FIXED / DATE / SEQUENCE）
     */
    private String type;

    /**
     * FIXED 专用：字面值
     */
    private String value;

    /**
     * DATE 专用：pattern 白名单
     */
    private String pattern;

    /**
     * SEQUENCE 专用：流水左补零宽度（必填）
     */
    private Integer length;

    /**
     * SEQUENCE 专用：起始值，默认 1
     */
    private Long start;

    /**
     * SEQUENCE 专用：步长，默认 1
     */
    private Long step;

    /**
     * 排序号（可空；前端拖拽编辑时使用）
     */
    private Integer sortNo;
}
