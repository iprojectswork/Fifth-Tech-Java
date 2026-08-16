package com.fifthtech.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author RH
 * @ClassName UserVO
 * @description: 用户视图
 * @date 2026年01月25日
 * @version: 1.0
 */
@Data
public class UserVO {

    /**
     * 用户 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 状态（0：未提交 1：已提交 2：已审核 10：审批中）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 成员组织名拼接（/ 分隔；list 视图用）
     */
    private String orgNames;

    /**
     * 成员组织详情
     */
    private List<UserOrgSummaryVO> orgs;

    /**
     * 已授角色
     */
    private List<UserRoleSummaryVO> roles;
}