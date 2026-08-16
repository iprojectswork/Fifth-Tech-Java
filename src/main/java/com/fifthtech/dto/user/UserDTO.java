package com.fifthtech.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

/**
 * @author RH
 * @ClassName UserDTO
 * @description: 用户数据传输对象
 * @date 2026年01月25日
 * @version: 1.0
 */
@Data
public class UserDTO {

    /**
     * 主键（edit 时必填）
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

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
     * 角色 ID 列表（null 表示不改）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<Long> roleIds;

    /**
     * 精确匹配该组织成员（list 端可选过滤）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orgId;
}