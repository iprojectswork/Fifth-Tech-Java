package com.fifthtech.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserVO
 *
 * <p>用户视图。
 * <ul>
 *   <li>{@code orgNames} / {@code orgs}：成员组织（只读展示；写入走组织管理）。</li>
 *   <li>{@code roles}：已授全局角色。</li>
 * </ul>
 * </p>
 *
 * @author RH
 * @description 用户视图对象
 * @date 2026-01-25
 */
@Data
public class UserVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 成员组织名拼接（{@code /} 分隔；list 视图用） */
    private String orgNames;

    /** 成员组织详情（详情 / 表单用） */
    private List<UserOrgSummaryVO> orgs;

    /** 已授角色（详情 / 表单用） */
    private List<UserRoleSummaryVO> roles;
}