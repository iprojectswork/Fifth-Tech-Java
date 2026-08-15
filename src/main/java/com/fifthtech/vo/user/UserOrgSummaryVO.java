package com.fifthtech.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * UserOrgSummaryVO
 *
 * <p>用户视图内嵌的成员组织摘要（{@code id / orgCode / orgName / pathName}）。</p>
 *
 * @author RH
 * @description 用户成员组织摘要
 * @date 2026-08-09
 */
@Data
public class UserOrgSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String orgCode;

    private String orgName;

    private String pathName;
}