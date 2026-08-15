package com.fifthtech.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

/**
 * UserDTO
 *
 * <p>用户数据传输对象。
 * <ul>
 *   <li>{@code roleIds}：角色 ID 列表（{@code sys_user_role}）；null 表示不改，非 null 全量替换。</li>
 *   <li>{@code orgId}：list 端可选过滤，精确匹配该组织成员。</li>
 *   <li>组织成员关系不在用户表单写入，由组织管理页维护。</li>
 * </ul>
 * </p>
 *
 * @author RH
 * @description 用户数据传输对象
 * @date 2026-01-25
 */
@Data
public class UserDTO {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    /** 状态（0：未提交 1：已提交 2：已审核 10：审批中；语义与角色/权限不一致，详见 AGENTS.md 备注） */
    private Integer status;

    /** 角色 ID 列表；null 表示不改 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<Long> roleIds;

    /** list 端可选过滤：精确匹配该组织成员 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orgId;
}