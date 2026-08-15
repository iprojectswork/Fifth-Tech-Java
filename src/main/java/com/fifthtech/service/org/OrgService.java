package com.fifthtech.service.org;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.dao.entity.org.Org;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dto.org.OrgDTO;
import com.fifthtech.dto.org.OrgMembersDTO;
import com.fifthtech.dto.org.OrgMoveDTO;
import com.fifthtech.dto.org.OrgQueryDTO;
import com.fifthtech.vo.org.OrgMemberVO;
import com.fifthtech.vo.org.OrgTreeVO;
import com.fifthtech.vo.org.OrgVO;

import java.util.List;
import java.util.Map;

/**
 * OrgService
 *
 * <p>组织服务。方法命名遵循 {@code JAVA-CODING-CONVENTIONS.md}：
 * {@code insert} / {@code edit} / {@code delete} / {@code list} / {@code info}；
 * 业务特例方法：{@code listChildren}（懒加载 children 别名）、
 * {@code tree}（全量树）、{@code move}（改挂父组织）、
 * {@code options}（启用组织选择器树 / 列表）、{@code usersByRole}
 * （C4 §4.7 查人）。</p>
 *
 * @author RH
 * @description 组织服务接口
 * @date 2026-08-09
 */
public interface OrgService extends IService<Org> {

    /**
     * 懒加载 children：返回 parentId 下所有直接子（含 hasChildren、path、typeLabel）
     */
    List<OrgVO> listChildren(OrgQueryDTO query);

    /**
     * 右栏 list：与 children 同数据源，方法别名
     */
    List<OrgVO> list(OrgQueryDTO query);

    /**
     * 全量树（含叶子与 path）
     */
    List<OrgTreeVO> tree();

    /**
     * 详情（含 path / hasChildren）
     */
    OrgVO info(Long id);

    /**
     * 新增组织
     */
    OrgVO insert(OrgDTO dto);

    /**
     * 修改组织（不改 parent；改挂请用 {@link #move}）
     */
    OrgVO edit(OrgDTO dto);

    /**
     * 改挂父组织（整树可拖；服务端权威校验；禁止挂到自己/子孙）
     */
    void move(OrgMoveDTO dto);

    /**
     * 逻辑删除（有未删子 / 成员 → 拒绝）
     */
    void delete(Long id);

    /**
     * 选择器：仅启用组织全量扁平列表（前端组装树用）
     */
    List<OrgVO> options();

    /**
     * 查人：组织成员 ∩ 持有该全局角色的用户
     */
    List<User> usersByRole(OrgQueryDTO query);

    /**
     * 本组织成员列表
     */
    List<OrgMemberVO> listMembers(OrgQueryDTO query);

    /**
     * 全量替换本组织成员
     */
    void replaceMembers(OrgMembersDTO dto);

    /**
     * 本组织及其全部下级的 ID（含自身）
     */
    List<Long> subtreeOrgIds(Long orgId);

    /**
     * 组织名称路径（例：总公司/研发中心/前端组）
     */
    Map<Long, String> pathNames(List<Long> orgIds);
}