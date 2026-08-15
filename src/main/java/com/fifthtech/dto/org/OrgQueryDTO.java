package com.fifthtech.dto.org;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * OrgQueryDTO
 *
 * @author RH
 * @description 组织查询条件（前端入参）
 * @date 2026-08-15
 */
@Data
public class OrgQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orgId;

    private String roleCode;
}
