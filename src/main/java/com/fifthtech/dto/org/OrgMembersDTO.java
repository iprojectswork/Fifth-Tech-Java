package com.fifthtech.dto.org;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author RH
 * @ClassName OrgMembersDTO
 * @description: 组织成员入参
 * @date 2026年08月15日
 * @version: 1.0
 */
@Data
public class OrgMembersDTO implements Serializable {

    /**
     * 序列化版本号
     */
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