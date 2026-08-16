package com.fifthtech.vo.org;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName OrgMemberVO
 * @description: 组织成员视图
 * @date 2026年08月15日
 * @version: 1.0
 */
@Data
public class OrgMemberVO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

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
     * 状态（0：未提交 1：已提交 2：已审核 10：审批中）
     */
    private Integer status;
}