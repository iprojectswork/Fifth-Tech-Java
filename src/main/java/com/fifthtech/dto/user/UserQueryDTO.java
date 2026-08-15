package com.fifthtech.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * UserQueryDTO
 *
 * @author RH
 * @description 用户查询条件（前端入参）
 * @date 2026-08-15
 */
@Data
public class UserQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer current;

    private Integer size;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private Integer status;

    /**
     * 精确匹配该组织成员（含下级）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orgId;

    /**
     * 由 orgId 展开后的组织 ID 集合（Service 填，Mapper 过滤用）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<Long> orgIds;

    /**
     * 路径上的用户 ID（/{id}/roles、/{id}/permissions）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
}
