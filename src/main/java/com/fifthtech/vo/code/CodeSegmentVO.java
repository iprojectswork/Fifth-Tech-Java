package com.fifthtech.vo.code;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * CodeSegmentVO
 *
 * @author RH
 * @description 编码片段视图对象
 * @date 2026-08-02
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeSegmentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private String value;
    private String pattern;
    private Integer length;
    private Long start;
    private Long step;
    private Integer sortNo;
}
