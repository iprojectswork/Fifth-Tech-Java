package com.fifthtech.vo.code;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName CodeSegmentVO
 * @description: 编码片段视图
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeSegmentVO implements Serializable {

    /**
     * 序列化版本号
     */
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
     * SEQUENCE 专用：流水左补零宽度
     */
    private Integer length;

    /**
     * SEQUENCE 专用：起始值
     */
    private Long start;

    /**
     * SEQUENCE 专用：步长
     */
    private Long step;

    /**
     * 排序号
     */
    private Integer sortNo;
}