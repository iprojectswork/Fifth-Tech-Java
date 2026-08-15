package com.fifthtech.service.dict;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.dao.entity.dict.DictNode;
import com.fifthtech.dto.dict.DictNodeDTO;
import com.fifthtech.dto.dict.DictNodeMoveDTO;
import com.fifthtech.dto.dict.DictNodeQueryDTO;
import com.fifthtech.vo.dict.DictNodeTreeVO;
import com.fifthtech.vo.dict.DictNodeVO;

import java.util.List;

/**
 * DictNodeService
 *
 * <p>数据字典节点服务。方法命名遵循 {@code JAVA-CODING-CONVENTIONS.md}：
 * {@code insert} / {@code edit} / {@code delete} / {@code list} / {@code info}；
 * 业务特例方法：{@code listChildren}（懒加载 children 别名）、
 * {@code tree}（全量树）、{@code move}（改挂父节点）、
 * {@code listDataByPathCode}（业务只读）。</p>
 *
 * @author RH
 * @description 数据字典节点服务接口
 * @date 2026-08-02
 */
public interface DictNodeService extends IService<DictNode> {

    /**
     * 懒加载 children：返回 parentId 下所有直接子（含 hasChildren 与 path）
     */
    List<DictNodeVO> listChildren(DictNodeQueryDTO query);

    /**
     * 右栏 list：与 children 同数据源，方法别名
     */
    List<DictNodeVO> list(DictNodeQueryDTO query);

    /**
     * 全量树（含叶子与 path）
     */
    List<DictNodeTreeVO> tree();

    /**
     * 详情（含 path / hasChildren）
     */
    DictNodeVO info(Long id);

    /**
     * 新增节点
     */
    DictNodeVO insert(DictNodeDTO dto);

    /**
     * 修改节点（不改 parent；改挂请用 {@link #move}）
     */
    DictNodeVO edit(DictNodeDTO dto);

    /**
     * 改挂父节点（仅无子节点可拖；服务端权威校验）
     */
    void move(DictNodeMoveDTO dto);

    /**
     * 逻辑删除（有未删子则拒绝）
     */
    void delete(Long id);

    /**
     * 业务只读：按 {@code pathCode} 精确匹配，取该节点下「启用」直接子
     */
    List<DictNodeVO> listDataByPathCode(DictNodeQueryDTO query);
}