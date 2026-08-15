package com.fifthtech.vo.org;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * OrgTreeVO
 *
 * <p>组织树节点视图：在 {@link OrgVO} 基础上追加 {@code children}。
 * 叶子 {@code children=[]} 且 {@code hasChildren=false}。</p>
 *
 * @author RH
 * @description 组织树节点视图
 * @date 2026-08-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrgTreeVO extends OrgVO {

    private static final long serialVersionUID = 1L;

    /**
     * 子节点（叶子为空列表）
     */
    private List<OrgTreeVO> children = new ArrayList<>();
}