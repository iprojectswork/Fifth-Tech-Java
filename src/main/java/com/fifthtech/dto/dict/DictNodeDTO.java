package com.fifthtech.dto.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * DictNodeDTO
 *
 * <p>数据字典节点新增 / 修改入参。
 * <ul>
 *   <li>{@code id}：edit 时必填，insert 时忽略。</li>
 *   <li>{@code parentId}：insert 时生效（指定挂到哪个父下；0 表示根层）；edit 时忽略，
 *   改挂请走 {@link DictNodeMoveDTO}。</li>
 *   <li>{@code code}：节点编码；不可含 /；trim 后非空；建议 {@code [A-Za-z0-9_\-\.]+}，长度 ≤ 64。</li>
 *   <li>{@code status}：1 启用 / 0 禁用。</li>
 * </ul>
 * </p>
 *
 * @author RH
 * @description 数据字典节点数据传输对象
 * @date 2026-08-02
 */
@Data
public class DictNodeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键（edit 时必填）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 父节点 ID（insert 时生效；0 表示根下第一层）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    /**
     * 节点编码
     */
    private String code;

    /**
     * 节点名称
     */
    private String name;

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