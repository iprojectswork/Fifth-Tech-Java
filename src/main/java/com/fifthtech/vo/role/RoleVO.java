package com.fifthtech.vo.role;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author RH
 * @ClassName RoleVO
 * @description: 角色视图
 * @date 2026年03月22日
 * @version: 1.0
 */
@Data
public class RoleVO {

    /**
     * 角色 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态（1：启用 0：禁用）
     */
    private Integer status;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}