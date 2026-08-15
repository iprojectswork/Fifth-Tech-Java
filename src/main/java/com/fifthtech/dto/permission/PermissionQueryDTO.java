package com.fifthtech.dto.permission;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * PermissionQueryDTO
 *
 * @author RH
 * @description 权限查询条件（前端入参）
 * @date 2026-08-15
 */
@Data
public class PermissionQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer current;

    private Integer size;

    private String permissionName;

    private String permissionCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;
}
