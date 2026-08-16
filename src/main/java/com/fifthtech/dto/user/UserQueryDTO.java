package com.fifthtech.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author RH
 * @ClassName UserQueryDTO
 * @description: 用户查询条件
 * @date 2026年08月15日
 * @version: 1.0
 */
@Data
public class UserQueryDTO implements Serializable {

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
     * 精确匹配该组织成员（含下级）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orgId;

    /**
     * 由 orgId 展开后的组织 ID 集合
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<Long> orgIds;

    /**
     * 路径上的用户 ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
}