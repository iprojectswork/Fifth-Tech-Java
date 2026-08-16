package com.fifthtech.vo.org;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RH
 * @ClassName OrgTreeVO
 * @description: 组织树节点视图
 * @date 2026年08月09日
 * @version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrgTreeVO extends OrgVO {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 子节点（叶子为空列表）
     */
    private List<OrgTreeVO> children = new ArrayList<>();
}