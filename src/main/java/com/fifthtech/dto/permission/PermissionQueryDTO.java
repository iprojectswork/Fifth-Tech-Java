package com.fifthtech.dto.permission;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName PermissionQueryDTO
 * @description: 权限查询条件
 * @date 2026年08月15日
 * @version: 1.0
 */
@Data
public class PermissionQueryDTO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private Integer current;

    /**
     * 每页条数
     */
    private Integer size;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 父权限 ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;
}