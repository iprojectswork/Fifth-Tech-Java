package com.fifthtech.dto.org;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * OrgMoveDTO
 *
 * <p>组织 move（改挂父组织）入参（C4 §4.5 / §5.1 / D15）。
 * 服务端权威校验：源存在且未删、源有未删子时拒绝、目标存在或为 0、非自己、
 * 新父下 code 不冲突、深度 ≤ 16、目标不能在源子树内（防环）。</p>
 *
 * @author RH
 * @description 组织 move 入参
 * @date 2026-08-09
 */
@Data
public class OrgMoveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 要移动的组织 ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 新的父组织 ID（0 表示移到根层）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long targetParentId;
}