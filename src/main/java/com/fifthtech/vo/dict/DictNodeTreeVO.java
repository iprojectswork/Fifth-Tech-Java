package com.fifthtech.vo.dict;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RH
 * @ClassName DictNodeTreeVO
 * @description: 数据字典树节点视图
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictNodeTreeVO extends DictNodeVO {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 子节点（叶子为空列表）
     */
    private List<DictNodeTreeVO> children = new ArrayList<>();
}