package com.fifthtech.vo.org;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * OrgVO
 *
 * <p>组织扁平视图。{@code pathCode} / {@code pathName} 由接口运行时按链拼接；
 * {@code hasChildren} 由 service 一次性批量判定后填入；
 * {@code orgTypeLabel} 由 service 按字典 {@code org/type} 翻译。</p>
 *
 * @author RH
 * @description 组织视图
 * @date 2026-08-09
 */
@Data
public class OrgVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    private String orgCode;
    private String orgName;
    private String orgType;
    private String orgTypeLabel;
    private Integer sort;
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

    /**
     * 组织编码路径（例：{@code HQ/RD/FE}；根下第一层无前导分隔符）
     */
    private String pathCode;

    /**
     * 组织名称路径（例：{@code 总公司/研发中心/前端组}）
     */
    private String pathName;

    /**
     * 是否有未删子组织
     */
    private Boolean hasChildren;
}