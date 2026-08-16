package com.fifthtech.vo.org;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author RH
 * @ClassName OrgVO
 * @description: 组织视图
 * @date 2026年08月09日
 * @version: 1.0
 */
@Data
public class OrgVO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 组织 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 父组织 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    /**
     * 组织编码
     */
    private String orgCode;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织类型（字典 org/type 的 code）
     */
    private String orgType;

    /**
     * 组织类型展示名
     */
    private String orgTypeLabel;

    /**
     * 排序
     */
    private Integer sort;

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

    /**
     * 组织编码路径
     */
    private String pathCode;

    /**
     * 组织名称路径
     */
    private String pathName;

    /**
     * 是否有未删子组织
     */
    private Boolean hasChildren;
}