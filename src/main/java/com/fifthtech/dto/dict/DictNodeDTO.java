package com.fifthtech.dto.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName DictNodeDTO
 * @description: 数据字典节点数据传输对象
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
public class DictNodeDTO implements Serializable {

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
     * 状态（1：启用 0：禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}