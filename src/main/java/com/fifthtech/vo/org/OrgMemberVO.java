package com.fifthtech.vo.org;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * OrgMemberVO
 *
 * <p>组织成员简表。</p>
 *
 * @author RH
 * @description 组织成员视图
 * @date 2026-08-15
 */
@Data
public class OrgMemberVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String username;

    private String nickname;

    private Integer status;
}
