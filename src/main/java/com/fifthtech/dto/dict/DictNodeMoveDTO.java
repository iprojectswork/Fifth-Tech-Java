package com.fifthtech.dto.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * DictNodeMoveDTO
 *
 * <p>数据字典节点 move（改挂父节点）入参。服务端权威校验：
 * 源存在且未删、源无未删子、目标存在或为 0、非自己、新父下 code 不冲突、深度 ≤ 16。</p>
 *
 * @author RH
 * @description 数据字典节点 move 入参
 * @date 2026-08-02
 */
@Data
public class DictNodeMoveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 要移动的节点 ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 新的父节点 ID（0 表示移到根层）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long targetParentId;
}