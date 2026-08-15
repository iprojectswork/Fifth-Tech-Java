package com.fifthtech.dto.dict;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * DictNodeQueryDTO
 *
 * @author RH
 * @description 数据字典查询条件（前端入参）
 * @date 2026-08-15
 */
@Data
public class DictNodeQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    private String pathCode;
}
