package com.fifthtech.vo.dict;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * DictNodeTreeVO
 *
 * <p>数据字典树节点视图：在 {@link DictNodeVO} 基础上追加 {@code children}。
 * 叶子 {@code children=[]} 且 {@code hasChildren=false}。</p>
 *
 * @author RH
 * @description 数据字典树节点视图
 * @date 2026-08-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictNodeTreeVO extends DictNodeVO {

    private static final long serialVersionUID = 1L;

    /**
     * 子节点（叶子为空列表）
     */
    private List<DictNodeTreeVO> children = new ArrayList<>();
}