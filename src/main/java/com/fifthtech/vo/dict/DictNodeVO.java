package com.fifthtech.vo.dict;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DictNodeVO
 *
 * <p>数据字典节点扁平视图。{@code pathCode} / {@code pathName} 由接口运行时按链拼接；
 * {@code hasChildren} 由 service 一次性批量判定后填入。</p>
 *
 * @author RH
 * @description 数据字典节点视图
 * @date 2026-08-02
 */
@Data
public class DictNodeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    private String code;
    private String name;
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
     * 节点编码路径（例：{@code system/user/status}；根下第一层无前导分隔符）
     */
    private String pathCode;

    /**
     * 节点名称路径（例：{@code 系统/用户/状态}）
     */
    private String pathName;

    /**
     * 是否有未删子节点
     */
    private Boolean hasChildren;
}