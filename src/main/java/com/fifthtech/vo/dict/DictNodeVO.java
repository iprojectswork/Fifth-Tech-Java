package com.fifthtech.vo.dict;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author RH
 * @ClassName DictNodeVO
 * @description: 数据字典节点视图
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
public class DictNodeVO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 节点 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 父节点 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
     * 节点编码路径
     */
    private String pathCode;

    /**
     * 节点名称路径
     */
    private String pathName;

    /**
     * 是否有未删子节点
     */
    private Boolean hasChildren;
}