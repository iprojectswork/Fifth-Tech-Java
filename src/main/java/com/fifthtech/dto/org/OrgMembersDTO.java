package com.fifthtech.dto.org;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * OrgMembersDTO
 *
 * <p>组织成员全量替换入参。</p>
 *
 * @author RH
 * @description 组织成员入参
 * @date 2026-08-15
 */
@Data
public class OrgMembersDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组织 ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orgId;

    /**
     * 成员用户 ID 列表（全量替换）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<Long> userIds;
}
