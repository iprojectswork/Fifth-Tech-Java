package com.fifthtech.dto.org;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName OrgDTO
 * @description: 组织数据传输对象
 * @date 2026年08月09日
 * @version: 1.0
 */
@Data
public class OrgDTO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键（edit 时必填）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 父组织 ID（insert 时生效；0 表示根下第一层）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
}