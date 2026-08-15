package com.fifthtech.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * UserRoleSummaryVO
 *
 * <p>用户视图内嵌的角色摘要（{@code id / roleCode / roleName}）。</p>
 *
 * @author RH
 * @description 用户角色摘要
 * @date 2026-08-09
 */
@Data
public class UserRoleSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String roleCode;

    private String roleName;
}
