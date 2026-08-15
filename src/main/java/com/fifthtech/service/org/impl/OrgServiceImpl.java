package com.fifthtech.service.org.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fifthtech.dao.entity.org.Org;
import com.fifthtech.dao.entity.org.UserOrg;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dao.entity.user.UserRole;
import com.fifthtech.dao.mapper.org.OrgMapper;
import com.fifthtech.dao.mapper.org.UserOrgMapper;
import com.fifthtech.dao.mapper.role.RoleMapper;
import com.fifthtech.dao.mapper.user.UserMapper;
import com.fifthtech.dao.mapper.user.UserRoleMapper;
import com.fifthtech.dto.dict.DictNodeQueryDTO;
import com.fifthtech.dto.org.OrgDTO;
import com.fifthtech.dto.org.OrgMembersDTO;
import com.fifthtech.dto.org.OrgMoveDTO;
import com.fifthtech.dto.org.OrgQueryDTO;
import com.fifthtech.security.UserContext;
import com.fifthtech.service.dict.DictNodeService;
import com.fifthtech.service.org.OrgService;
import com.fifthtech.vo.dict.DictNodeVO;
import com.fifthtech.vo.org.OrgMemberVO;
import com.fifthtech.vo.org.OrgTreeVO;
import com.fifthtech.vo.org.OrgVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * OrgServiceImpl
 *
 * <p>规则（C4 §3 / §4 / §5 / §7.3）摘要：
 * <ul>
 *   <li>D4/D5：同父未删 org_code 唯一（DB 部分唯一索引兜底）；code 禁含 /，trim 后非空，
 *   长度 1~64，建议 {@code [A-Za-z0-9_\-\.]+}。</li>
 *   <li>D6：path 不落库，运行时按链拼接；getParent 步数超过 {@link #MAX_DEPTH} 视为坏数据。</li>
 *   <li>D7：列表/树子节点顺序 {@code sort ASC, org_code ASC, id ASC}。</li>
 *   <li>D8：业务读 options 仅返回 status=1。</li>
     *   <li>D9/D14/D17：删除要求无未删子/成员；move 允许整树，目标必须存在或为 0、
     *   id != targetParentId、目标不在源子树内（防环）。</li>
 *   <li>D15：组织 move 是整树 move；下属组织的 org_id 也不变（parent_id 自关联）。</li>
 *   <li>D18：最大深度 16（根下第一层 depth=1）。</li>
     *   <li>D14：删除组织若有子/成员 → 拒绝。</li>
 * </ul>
 * </p>
 *
 * @author RH
 * @description 组织服务实现
 * @date 2026-08-09
 */
@Service
public class OrgServiceImpl extends ServiceImpl<OrgMapper, Org> implements OrgService {

    /** 最大允许深度（C4 D18，根下第一层 depth=1） */
    private static final int MAX_DEPTH = 16;

    /** org_code 字符合法集（C4 D5：禁 /；trim 后非空；长度 1~64） */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_\\-\\.]+$");

    /** 根节点 parent_id */
    private static final long ROOT_ID = 0L;

    private static final String ORG_TYPE_PATH = "org/type";

    @Resource
    private OrgMapper orgMapper;

    @Resource
    private UserOrgMapper userOrgMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private DictNodeService dictNodeService;

    // ---------------------------------------------------------------------
    // listChildren / list
    // ---------------------------------------------------------------------

    @Override
    public List<OrgVO> listChildren(OrgQueryDTO query) {
        return listDirectChildren(query == null ? null : query.getParentId());
    }

    @Override
    public List<OrgVO> list(OrgQueryDTO query) {
        return listDirectChildren(query == null ? null : query.getParentId());
    }

    private List<OrgVO> listDirectChildren(Long parentId) {
        Long pid = parentId == null ? ROOT_ID : parentId;
        List<Org> children = orgMapper.selectChildrenByParentId(pid);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }
        // 父 path（仅当 parentId != 0 时走链）
        String[] parentPath = pid == ROOT_ID ? new String[]{"", ""} : computePathById(pid);
        // 批量 hasChildren
        List<Long> childIds = new ArrayList<>(children.size());
        for (Org c : children) {
            childIds.add(c.getId());
        }
        Set<Long> hasChildrenSet = new HashSet<>(orgMapper.selectActiveParentIdsWithChildren(childIds));

        List<OrgVO> vos = new ArrayList<>(children.size());
        for (Org c : children) {
            OrgVO vo = toFlatVO(c);
            String pc = parentPath[0];
            String pn = parentPath[1];
            vo.setPathCode(pc.isEmpty() ? c.getOrgCode() : pc + "/" + c.getOrgCode());
            vo.setPathName(pn.isEmpty() ? c.getOrgName() : pn + "/" + c.getOrgName());
            vo.setHasChildren(hasChildrenSet.contains(c.getId()));
            vos.add(vo);
        }
        return vos;
    }

    // ---------------------------------------------------------------------
    // tree
    // ---------------------------------------------------------------------

    @Override
    public List<OrgTreeVO> tree() {
        List<Org> all = orgMapper.selectActiveList();
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, List<Org>> childrenByParent = new HashMap<>();
        for (Org n : all) {
            List<Org> bucket = childrenByParent.computeIfAbsent(n.getParentId(), k -> new ArrayList<>());
            bucket.add(n);
        }
        List<Org> roots = childrenByParent.getOrDefault(ROOT_ID, Collections.emptyList());
        List<OrgTreeVO> tree = new ArrayList<>(roots.size());
        for (Org r : roots) {
            tree.add(buildTreeNode(r, "", "", childrenByParent));
        }
        return tree;
    }

    private OrgTreeVO buildTreeNode(Org node,
                                    String parentPathCode,
                                    String parentPathName,
                                    Map<Long, List<Org>> childrenByParent) {
        OrgTreeVO vo = new OrgTreeVO();
        copyFlatFields(node, vo);
        String pc = parentPathCode.isEmpty() ? node.getOrgCode() : parentPathCode + "/" + node.getOrgCode();
        String pn = parentPathName.isEmpty() ? node.getOrgName() : parentPathName + "/" + node.getOrgName();
        vo.setPathCode(pc);
        vo.setPathName(pn);
        List<Org> kids = childrenByParent.getOrDefault(node.getId(), Collections.emptyList());
        vo.setHasChildren(!kids.isEmpty());
        for (Org k : kids) {
            vo.getChildren().add(buildTreeNode(k, pc, pn, childrenByParent));
        }
        return vo;
    }

    // ---------------------------------------------------------------------
    // info
    // ---------------------------------------------------------------------

    @Override
    public OrgVO info(Long id) {
        if (id == null) {
            return null;
        }
        Org node = orgMapper.selectById(id);
        if (node == null) {
            return null;
        }
        OrgVO vo = toFlatVO(node);
        String[] path = computePathById(id);
        if (path != null) {
            vo.setPathCode(path[0]);
            vo.setPathName(path[1]);
        } else {
            // 兜底：走链失败则至少填自身
            vo.setPathCode(node.getOrgCode());
            vo.setPathName(node.getOrgName());
        }
        Set<Long> hasKids = new HashSet<>(
                orgMapper.selectActiveParentIdsWithChildren(Collections.singletonList(id)));
        vo.setHasChildren(hasKids.contains(id));
        return vo;
    }

    // ---------------------------------------------------------------------
    // insert
    // ---------------------------------------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrgVO insert(OrgDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        String code = normalizeCode(dto.getOrgCode());
        String name = normalizeName(dto.getOrgName());
        String orgTypeCode = requireOrgType(dto.getOrgType());
        Integer status = dto.getStatus();
        if (status != null && status != 0 && status != 1) {
            throw new IllegalArgumentException("status 仅支持 0/1");
        }
        Long parentId = dto.getParentId() == null ? ROOT_ID : dto.getParentId();
        if (parentId != ROOT_ID) {
            Org parent = orgMapper.selectById(parentId);
            if (parent == null) {
                throw new IllegalArgumentException("父组织不存在");
            }
        }
        // 深度校验：parent.depth + 1 <= 16；坏链 depth=-1 直接拒绝
        int parentDepth = parentId == ROOT_ID ? 0 : computeDepth(parentId);
        if (parentDepth < 0) {
            throw new IllegalArgumentException("父组织链路异常，无法新增");
        }
        if (parentDepth + 1 > MAX_DEPTH) {
            throw new IllegalArgumentException("超过最大深度 " + MAX_DEPTH);
        }
        // 同父 code 唯一（DB 部分唯一索引兜底）
        if (existsSameCode(parentId, code, null)) {
            throw new IllegalArgumentException("同父下 orgCode 已存在");
        }
        Org entity = new Org();
        entity.setParentId(parentId);
        entity.setOrgCode(code);
        entity.setOrgName(name);
        entity.setOrgType(orgTypeCode);
        entity.setSort(dto.getSort() == null ? 0 : dto.getSort());
        entity.setStatus(status == null ? 1 : status);
        entity.setRemark(dto.getRemark() == null ? null : dto.getRemark().trim());
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        Long uid = currentUserIdOrNull();
        if (uid != null) {
            entity.setCreateId(uid);
            entity.setUpdateId(uid);
        }
        orgMapper.insert(entity);
        return info(entity.getId());
    }

    // ---------------------------------------------------------------------
    // edit
    // ---------------------------------------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrgVO edit(OrgDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        Org existing = orgMapper.selectById(dto.getId());
        if (existing == null) {
            throw new IllegalArgumentException("组织不存在");
        }
        // org_type 改了才校验
        if (dto.getOrgType() != null && !dto.getOrgType().equals(existing.getOrgType())) {
            existing.setOrgType(requireOrgType(dto.getOrgType()));
        }
        // orgCode 改了才校验（忽略 parentId，不允许通过 edit 改挂）；落库使用 trim 后值
        if (dto.getOrgCode() != null) {
            String code = normalizeCode(dto.getOrgCode());
            if (!code.equals(existing.getOrgCode())) {
                if (existsSameCode(existing.getParentId(), code, existing.getId())) {
                    throw new IllegalArgumentException("同父下 orgCode 已存在");
                }
                existing.setOrgCode(code);
            }
        }
        if (dto.getOrgName() != null) {
            existing.setOrgName(normalizeName(dto.getOrgName()));
        }
        if (dto.getSort() != null) {
            existing.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new IllegalArgumentException("status 仅支持 0/1");
            }
            existing.setStatus(dto.getStatus());
        }
        if (dto.getRemark() != null) {
            existing.setRemark(dto.getRemark());
        }
        existing.setUpdateTime(LocalDateTime.now());
        Long uid = currentUserIdOrNull();
        if (uid != null) {
            existing.setUpdateId(uid);
        }
        updateById(existing);
        return info(existing.getId());
    }

    // ---------------------------------------------------------------------
    // move
    // ---------------------------------------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void move(OrgMoveDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        Long id = dto.getId();
        Long targetParentId = dto.getTargetParentId();
        if (id == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        if (targetParentId == null) {
            throw new IllegalArgumentException("targetParentId 不能为空");
        }
        if (id.equals(targetParentId)) {
            throw new IllegalArgumentException("不能把自己设为父");
        }
        Org source = orgMapper.selectById(id);
        if (source == null) {
            throw new IllegalArgumentException("组织不存在");
        }
        // 目标存在或为 0
        if (targetParentId != ROOT_ID) {
            Org tp = orgMapper.selectById(targetParentId);
            if (tp == null) {
                throw new IllegalArgumentException("目标父组织不存在");
            }
            // 防环：目标不能在源子树内
            if (isDescendant(targetParentId, id)) {
                throw new IllegalArgumentException("目标父组织在源子树内，无法移动");
            }
        }
        // 新父下 code 唯一（排除自身）
        if (existsSameCode(targetParentId, source.getOrgCode(), id)) {
            throw new IllegalArgumentException("目标父下 orgCode 已存在");
        }
        int targetDepth = targetParentId == ROOT_ID ? 0 : computeDepth(targetParentId);
        if (targetDepth < 0) {
            throw new IllegalArgumentException("目标父组织链路异常，无法移动");
        }
        int sourceSubtreeHeight = computeSubtreeHeight(id);
        if (targetDepth + sourceSubtreeHeight > MAX_DEPTH) {
            throw new IllegalArgumentException("超过最大深度 " + MAX_DEPTH);
        }
        Org upd = new Org();
        upd.setId(id);
        upd.setParentId(targetParentId);
        upd.setUpdateTime(LocalDateTime.now());
        Long uid = currentUserIdOrNull();
        if (uid != null) {
            upd.setUpdateId(uid);
        }
        updateById(upd);
    }

    /**
     * 防环判定：{@code candidate} 是否在 {@code ancestorId} 的子树内（不含 ancestorId 本身）。
     * 走 parent 链遇成环或超过 {@link #MAX_DEPTH} 一律视为 false（保守放行后由后续唯一性约束兜底）。
     */
    private boolean isDescendant(Long candidate, Long ancestorId) {
        if (candidate == null || ancestorId == null) {
            return false;
        }
        Set<Long> visited = new HashSet<>();
        Long current = candidate;
        int hops = 0;
        while (current != null && current != ROOT_ID) {
            if (!visited.add(current)) {
                return false;
            }
            if (++hops > MAX_DEPTH + 4) {
                return true;
            }
            if (current.equals(ancestorId)) {
                return true;
            }
            Org node = orgMapper.selectById(current);
            if (node == null) {
                return false;
            }
            current = node.getParentId();
        }
        return false;
    }

    // ---------------------------------------------------------------------
    // delete
    // ---------------------------------------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        Org existing = orgMapper.selectById(id);
        if (existing == null) {
            return;
        }
        long children = orgMapper.countActiveChildrenByParentId(id);
        if (children > 0) {
            throw new IllegalArgumentException("存在子组织，不允许删除");
        }
        long members = orgMapper.countActiveMembersByOrgId(id);
        if (members > 0) {
            throw new IllegalArgumentException("存在成员用户，不允许删除");
        }
        Long uid = currentUserIdOrNull();
        Org upd = new Org();
        upd.setId(id);
        if (uid != null) {
            upd.setDeleteId(uid);
        }
        upd.setDeleteTime(LocalDateTime.now());
        updateById(upd);
        // 触发 @TableLogic → UPDATE ... SET deleted = 1 WHERE id = ? AND deleted = 0
        orgMapper.deleteById(id);
    }

    // ---------------------------------------------------------------------
    // options
    // ---------------------------------------------------------------------

    @Override
    public List<OrgVO> options() {
        List<Org> all = orgMapper.selectEnabledOptions();
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        // 一次性拼 path：按 parent 分桶就地拼
        Map<Long, List<Org>> childrenByParent = new HashMap<>();
        for (Org n : all) {
            childrenByParent.computeIfAbsent(n.getParentId(), k -> new ArrayList<>()).add(n);
        }
        // 路径前缀表：id -> {code, name}
        Map<Long, String[]> pathById = new HashMap<>();
        List<OrgVO> result = new ArrayList<>(all.size());
        // 递归构造：根层先
        List<Org> roots = childrenByParent.getOrDefault(ROOT_ID, Collections.emptyList());
        for (Org r : roots) {
            pathById.put(r.getId(), new String[]{r.getOrgCode(), r.getOrgName()});
            result.add(toVOWithPath(r, r.getOrgCode(), r.getOrgName(), pathById, childrenByParent));
        }
        return result;
    }

    private OrgVO toVOWithPath(Org node,
                               String pc,
                               String pn,
                               Map<Long, String[]> pathById,
                               Map<Long, List<Org>> childrenByParent) {
        OrgVO vo = toFlatVO(node);
        vo.setPathCode(pc);
        vo.setPathName(pn);
        List<Org> kids = childrenByParent.getOrDefault(node.getId(), Collections.emptyList());
        vo.setHasChildren(!kids.isEmpty());
        // 这里仅返回扁平 + path，前端自己组装树；选择器类型按需
        return vo;
    }

    // ---------------------------------------------------------------------
    // usersByRole
    // ---------------------------------------------------------------------

    @Override
    public List<User> usersByRole(OrgQueryDTO query) {
        if (query == null || query.getOrgId() == null) {
            throw new IllegalArgumentException("orgId 不能为空");
        }
        Long orgId = query.getOrgId();
        String roleCode = query.getRoleCode();
        if (roleCode == null || roleCode.trim().isEmpty()) {
            throw new IllegalArgumentException("roleCode 不能为空");
        }
        Org org = orgMapper.selectById(orgId);
        if (org == null) {
            throw new IllegalArgumentException("组织不存在");
        }
        List<Long> memberIds = userIdsInSubtree(orgId);
        if (memberIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Role> rw = new LambdaQueryWrapper<>();
        rw.eq(Role::getRoleCode, roleCode.trim())
                .eq(Role::getDeleted, 0)
                .eq(Role::getStatus, 1);
        List<Role> roles = roleMapper.selectList(rw);
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> roleIds = new ArrayList<>(roles.size());
        for (Role r : roles) {
            roleIds.add(r.getId());
        }
        LambdaQueryWrapper<UserRole> urw = new LambdaQueryWrapper<>();
        urw.in(UserRole::getRoleId, roleIds);
        List<UserRole> urs = userRoleMapper.selectList(urw);
        if (urs == null || urs.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> memberSet = new HashSet<>(memberIds);
        Set<Long> userIds = new HashSet<>();
        for (UserRole ur : urs) {
            if (memberSet.contains(ur.getUserId())) {
                userIds.add(ur.getUserId());
            }
        }
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<User> uw = new LambdaQueryWrapper<>();
        uw.in(User::getId, userIds)
                .eq(User::getDeleted, 0)
                .orderByAsc(User::getId);
        return userMapper.selectList(uw);
    }

    @Override
    public List<OrgMemberVO> listMembers(OrgQueryDTO query) {
        if (query == null || query.getOrgId() == null) {
            throw new IllegalArgumentException("orgId 不能为空");
        }
        Long orgId = query.getOrgId();
        Org org = orgMapper.selectById(orgId);
        if (org == null) {
            throw new IllegalArgumentException("组织不存在");
        }
        List<Long> userIds = userIdsInSubtree(orgId);
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<User> uw = new LambdaQueryWrapper<>();
        uw.in(User::getId, userIds)
                .eq(User::getDeleted, 0)
                .orderByAsc(User::getId);
        List<User> users = userMapper.selectList(uw);
        List<OrgMemberVO> result = new ArrayList<>();
        if (users == null) {
            return result;
        }
        for (User u : users) {
            OrgMemberVO vo = new OrgMemberVO();
            vo.setId(u.getId());
            vo.setUsername(u.getUsername());
            vo.setNickname(u.getNickname());
            vo.setStatus(u.getStatus());
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceMembers(OrgMembersDTO dto) {
        if (dto == null || dto.getOrgId() == null) {
            throw new IllegalArgumentException("orgId 不能为空");
        }
        Long orgId = dto.getOrgId();
        Org org = orgMapper.selectById(orgId);
        if (org == null) {
            throw new IllegalArgumentException("组织不存在");
        }
        Set<Long> newSet = new HashSet<>();
        if (dto.getUserIds() != null) {
            for (Long uid : dto.getUserIds()) {
                if (uid != null) {
                    newSet.add(uid);
                }
            }
        }
        if (!newSet.isEmpty()) {
            LambdaQueryWrapper<User> uw = new LambdaQueryWrapper<>();
            uw.in(User::getId, newSet).eq(User::getDeleted, 0);
            Long existed = userMapper.selectCount(uw);
            if (existed == null || existed != newSet.size()) {
                throw new IllegalArgumentException("部分用户不存在或已删除");
            }
        }
        List<Long> subtree = subtreeOrgIds(orgId);
        Set<Long> oldSet = new HashSet<>(userIdsInSubtree(orgId));
        for (Long uid : oldSet) {
            if (!newSet.contains(uid)) {
                userOrgMapper.deleteByUserIdAndOrgIds(uid, subtree);
            }
        }
        for (Long uid : newSet) {
            if (!oldSet.contains(uid)) {
                UserOrg row = new UserOrg();
                row.setUserId(uid);
                row.setOrgId(orgId);
                row.setCreateTime(LocalDateTime.now());
                userOrgMapper.insert(row);
            }
        }
    }

    @Override
    public List<Long> subtreeOrgIds(Long orgId) {
        if (orgId == null) {
            return new ArrayList<>();
        }
        List<Org> all = orgMapper.selectActiveList();
        if (all == null || all.isEmpty()) {
            return new ArrayList<>(List.of(orgId));
        }
        Map<Long, List<Long>> children = new HashMap<>();
        boolean found = false;
        for (Org node : all) {
            if (orgId.equals(node.getId())) {
                found = true;
            }
            children.computeIfAbsent(node.getParentId(), k -> new ArrayList<>()).add(node.getId());
        }
        if (!found && orgMapper.selectById(orgId) == null) {
            return new ArrayList<>();
        }
        List<Long> result = new ArrayList<>();
        List<Long> stack = new ArrayList<>();
        stack.add(orgId);
        while (!stack.isEmpty()) {
            Long id = stack.remove(stack.size() - 1);
            result.add(id);
            List<Long> kids = children.get(id);
            if (kids != null) {
                stack.addAll(kids);
            }
        }
        return result;
    }

    @Override
    public Map<Long, String> pathNames(List<Long> orgIds) {
        Map<Long, String> out = new HashMap<>();
        if (orgIds == null || orgIds.isEmpty()) {
            return out;
        }
        for (Long id : orgIds) {
            if (id == null) {
                continue;
            }
            String[] path = computePathById(id);
            out.put(id, path == null ? "" : path[1]);
        }
        return out;
    }

    private List<Long> userIdsInSubtree(Long orgId) {
        List<Long> subtree = subtreeOrgIds(orgId);
        if (subtree.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> userIds = userOrgMapper.selectUserIdsByOrgIds(subtree);
        return userIds == null ? new ArrayList<>() : userIds;
    }

    // ---------------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------------

    /**
     * 走 parent 链拼 pathCode / pathName（不超过 MAX_DEPTH 步数；步数超限或成环返回 null）。
     */
    private String[] computePathById(Long id) {
        if (id == null) {
            return null;
        }
        List<String[]> segs = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        Long current = id;
        int hops = 0;
        while (current != null && current != ROOT_ID) {
            if (!visited.add(current)) {
                return null; // 成环
            }
            if (++hops > MAX_DEPTH + 4) {
                return null;
            }
            Org node = orgMapper.selectById(current);
            if (node == null) {
                return null;
            }
            segs.add(0, new String[]{node.getOrgCode(), node.getOrgName()});
            current = node.getParentId();
        }
        if (segs.isEmpty()) {
            return null;
        }
        StringBuilder pc = new StringBuilder();
        StringBuilder pn = new StringBuilder();
        for (int i = 0; i < segs.size(); i++) {
            if (i > 0) {
                pc.append('/');
                pn.append('/');
            }
            pc.append(segs.get(i)[0]);
            pn.append(segs.get(i)[1]);
        }
        return new String[]{pc.toString(), pn.toString()};
    }

    /**
     * 计算节点深度（根下第一层 = 1；根 = 0）。走 parent 链遇 NULL / 成环 / 超 MAX_DEPTH 返回 -1。
     */
    private int computeDepth(Long id) {
        if (id == null || id == ROOT_ID) {
            return 0;
        }
        Set<Long> visited = new HashSet<>();
        Long current = id;
        int depth = 0;
        while (current != null && current != ROOT_ID) {
            if (!visited.add(current)) {
                return -1;
            }
            if (++depth > MAX_DEPTH) {
                return -1;
            }
            Org node = orgMapper.selectById(current);
            if (node == null) {
                return -1;
            }
            current = node.getParentId();
        }
        return depth;
    }

    private int computeSubtreeHeight(Long rootId) {
        if (rootId == null) {
            return 0;
        }
        List<Org> children = orgMapper.selectChildrenByParentId(rootId);
        if (children == null || children.isEmpty()) {
            return 1;
        }
        int maxChild = 0;
        for (Org child : children) {
            int h = computeSubtreeHeight(child.getId());
            if (h > maxChild) {
                maxChild = h;
            }
        }
        return 1 + maxChild;
    }

    private boolean existsSameCode(Long parentId, String orgCode, Long excludeId) {
        LambdaQueryWrapper<Org> w = new LambdaQueryWrapper<>();
        w.eq(Org::getParentId, parentId)
                .eq(Org::getOrgCode, orgCode)
                .eq(Org::getDeleted, 0);
        if (excludeId != null) {
            w.ne(Org::getId, excludeId);
        }
        return orgMapper.selectCount(w) > 0;
    }

    /**
     * 校验并返回 trim 后的 orgCode（C4 D5）；调用方必须用返回值落库。
     */
    private String normalizeCode(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("orgCode 不能为空");
        }
        String code = raw.trim();
        if (code.isEmpty()) {
            throw new IllegalArgumentException("orgCode 不能为空");
        }
        if (code.length() > 64) {
            throw new IllegalArgumentException("orgCode 长度不能超过 64");
        }
        if (code.indexOf('/') >= 0) {
            throw new IllegalArgumentException("orgCode 不能包含 /");
        }
        if (!CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("orgCode 仅支持字母、数字、下划线、连字符、点");
        }
        return code;
    }

    /**
     * 校验并返回 trim 后的 orgName；禁止空名（DB NOT NULL）。
     */
    private String normalizeName(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("orgName 不能为空");
        }
        String name = raw.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("orgName 不能为空");
        }
        if (name.length() > 128) {
            throw new IllegalArgumentException("orgName 长度不能超过 128");
        }
        return name;
    }

    private OrgVO toFlatVO(Org n) {
        OrgVO vo = new OrgVO();
        copyFlatFields(n, vo);
        return vo;
    }

    private void copyFlatFields(Org n, OrgVO vo) {
        vo.setId(n.getId());
        vo.setParentId(n.getParentId());
        vo.setOrgCode(n.getOrgCode());
        vo.setOrgName(n.getOrgName());
        vo.setOrgType(n.getOrgType());
        vo.setOrgTypeLabel(orgTypeLabel(n.getOrgType()));
        vo.setSort(n.getSort());
        vo.setStatus(n.getStatus());
        vo.setRemark(n.getRemark());
        vo.setCreateId(n.getCreateId());
        vo.setCreateName(n.getCreateName());
        vo.setCreateTime(n.getCreateTime());
        vo.setUpdateId(n.getUpdateId());
        vo.setUpdateName(n.getUpdateName());
        vo.setUpdateTime(n.getUpdateTime());
    }

    private String requireOrgType(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("orgType 不能为空");
        }
        String code = raw.trim();
        Map<String, String> labels = orgTypeLabelMap();
        if (!labels.containsKey(code)) {
            throw new IllegalArgumentException("组织类型不在字典 org/type 中");
        }
        return code;
    }

    private String orgTypeLabel(String code) {
        if (code == null || code.isEmpty()) {
            return "";
        }
        String label = orgTypeLabelMap().get(code);
        return label == null ? code : label;
    }

    private Map<String, String> orgTypeLabelMap() {
        Map<String, String> map = new HashMap<>();
        try {
            DictNodeQueryDTO typeQuery = new DictNodeQueryDTO();
            typeQuery.setPathCode(ORG_TYPE_PATH);
            List<DictNodeVO> items = dictNodeService.listDataByPathCode(typeQuery);
            if (items != null) {
                for (DictNodeVO item : items) {
                    if (item.getCode() != null) {
                        map.put(item.getCode(), item.getName());
                    }
                }
            }
        } catch (IllegalArgumentException ignore) {
            // 字典未灌数时允许空映射，校验会拒绝写入
        }
        return map;
    }

    private Long currentUserIdOrNull() {
        try {
            return UserContext.getCurrentUserId();
        } catch (RuntimeException ignore) {
            return null;
        }
    }
}