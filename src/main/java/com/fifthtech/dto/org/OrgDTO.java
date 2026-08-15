package com.fifthtech.dto.org;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * OrgDTO
 *
 * <p>组织新增 / 修改入参（C4 §7.2）。
 * <ul>
 *   <li>{@code id}：edit 时必填，insert 时忽略。</li>
 *   <li>{@code parentId}：insert 时生效（指定挂到哪个父下；0 表示根层）；edit 时忽略，
 *   改挂请走 {@link OrgMoveDTO}。</li>
 *   <li>{@code orgCode}：组织编码；不可含 /；trim 后非空；建议 {@code [A-Za-z0-9_\-\.]+}，长度 ≤ 64。</li>
 *   <li>{@code orgType}：数据字典 {@code org/type} 的 code。</li>
 *   <li>{@code status}：1 启用 / 0 禁用。</li>
 * </ul>
 * </p>
 *
 * @author RH
 * @description 组织数据传输对象
 * @date 2026-08-09
 */
@Data
public class OrgDTO implements Serializable {

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
     * 状态（1 启用 / 0 禁用；默认 1）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}