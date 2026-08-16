package com.fifthtech.dto.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName DictNodeMoveDTO
 * @description: 数据字典节点 move 入参
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
public class DictNodeMoveDTO implements Serializable {

    /**
     * 序列化版本号
     */
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