package com.fifthtech.dto.org;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName OrgMoveDTO
 * @description: 组织 move 入参
 * @date 2026年08月09日
 * @version: 1.0
 */
@Data
public class OrgMoveDTO implements Serializable {

    /**
     * 序列化版本号
     */
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