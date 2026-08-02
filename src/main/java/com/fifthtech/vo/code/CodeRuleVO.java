package com.fifthtech.vo.code;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CodeRuleVO
 *
 * <p>编码规则视图对象。{@code segments} 以强类型列表返回（前端按 type 渲染）。
 * 雪花 id 序列化为字符串，避免 JS Number 精度丢失。</p>
 *
 * @author RH
 * @description 编码规则视图
 * @date 2026-08-02
 */
@Data
public class CodeRuleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String ruleCode;
    private String ruleName;
    private List<CodeSegmentVO> segments;
    private Integer batchSize;
    private Integer status;
    private String remark;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long createId;
    private String createName;
    private LocalDateTime createTime;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateId;
    private String updateName;
    private LocalDateTime updateTime;
}
