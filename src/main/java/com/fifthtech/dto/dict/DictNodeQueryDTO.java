package com.fifthtech.dto.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName DictNodeQueryDTO
 * @description: 数据字典查询条件
 * @date 2026年08月15日
 * @version: 1.0
 */
@Data
public class DictNodeQueryDTO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 父节点 ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    /**
     * 节点编码路径
     */
    private String pathCode;
}