package com.fifthtech.dto.org;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName OrgQueryDTO
 * @description: 组织查询条件
 * @date 2026年08月15日
 * @version: 1.0
 */
@Data
public class OrgQueryDTO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 父组织 ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    /**
     * 组织 ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orgId;

    /**
     * 角色编码
     */
    private String roleCode;
}