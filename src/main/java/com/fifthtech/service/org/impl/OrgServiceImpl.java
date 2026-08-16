package com.fifthtech.service.org.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fifthtech.common.BizConstants;
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
 * @author RH
 * @ClassName OrgServiceImpl
 * @description: 组织服务实现
 * @date 2026年08月09日
 * @version: 1.0
 */
@Service
public class OrgServiceImpl extends ServiceImpl<OrgMapper, Org> implements OrgService {

    /**
     * 沿 parent 链多走几步，用于识别坏链
     */
    private static final int TREE_WALK_SLACK = 4;

    /**
     * org_code 字符合法集（禁 /；trim 后非空；长度 1~64）
     */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_\\-\\.]+$");

    /**
     * 组织类型字典路径
     */
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

    @Override
    public List<OrgVO> listChildren(OrgQueryDTO query) {
        return listDirectChildren(query == null ? null : query.getParentId());
    }

    @Override
    public List<OrgVO> list(OrgQueryDTO query) {
        return listDirectChildren(query == null ? null : query.getParentId());
    }

    private List<OrgVO> listDirectChildren(Long parentId) {
        Long resolvedParentId = parentId == null ? BizConstants.ROOT_PARENT_ID : parentId;
        List<Org> children = orgMapper.selectChildrenByParentId(resolvedParentId);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }
        String[] parentPath = resolvedParentId == BizConstants.ROOT_PARENT_ID
                ? new String[]{"", ""} : computePathById(resolvedParentId);
        List<Long> childIds = new ArrayList<>(children.size());
        for (Org child : children) {
            childIds.add(child.getId());
        }
        Set<Long> hasChildrenSet = new HashSet<>(orgMapper.selectActiveParentIdsWithChildren(childIds));

        List<OrgVO> vos = new ArrayList<>(children.size());
        for (Org child : children) {
            OrgVO vo = toFlatVO(child);
            String pathCode = parentPath[0];
            String pathName = parentPath[1];
            vo.setPathCode(pathCode.isEmpty() ? child.getOrgCode() : pathCode + "/" + child.getOrgCode());
            vo.setPathName(pathName.isEmpty() ? child.getOrgName() : pathName + "/" + child.getOrgName());
            vo.setHasChildren(hasChildrenSet.contains(child.getId()));
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<OrgTreeVO> tree() {
        List<Org> all = orgMapper.selectActiveList();
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, List<Org>> childrenByParent = new HashMap<>();
        for (Org node : all) {
            List<Org> bucket = childrenByParent.computeIfAbsent(node.getParentId(), ignored -> new ArrayList<>());
            bucket.add(node);
        }
        List<Org> roots = childrenByParent.getOrDefault(BizConstants.ROOT_PARENT_ID, Collections.emptyList());
        List<OrgTreeVO> tree = new ArrayList<>(roots.size());
        for (Org root : roots) {
            tree.add(buildTreeNode(root, "", "", childrenByParent));
        }
        return tree;
    }

    private OrgTreeVO buildTreeNode(Org node,
                                    String parentPathCode,
                                    String parentPathName,
                                    Map<Long, List<Org>> childrenByParent) {
        OrgTreeVO vo = new OrgTreeVO();
        copyFlatFields(node, vo);
        String pathCode = parentPathCode.isEmpty() ? node.getOrgCode() : parentPathCode + "/" + node.getOrgCode();
        String pathName = parentPathName.isEmpty() ? node.getOrgName() : parentPathName + "/" + node.getOrgName();
        vo.setPathCode(pathCode);
        vo.setPathName(pathName);
        List<Org> childNodes = childrenByParent.getOrDefault(node.getId(), Collections.emptyList());
        vo.setHasChildren(!childNodes.isEmpty());
        for (Org child : childNodes) {
            vo.getChildren().add(buildTreeNode(child, pathCode, pathName, childrenByParent));
        }
        return vo;
    }

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
        if (status != null && status != BizConstants.STATUS_DISABLED && status != BizConstants.STATUS_ENABLED) {
            throw new IllegalArgumentException("status 仅支持 0/1");
        }
        Long parentId = dto.getParentId() == null ? BizConstants.ROOT_PARENT_ID : dto.getParentId();
        if (parentId != BizConstants.ROOT_PARENT_ID) {
            Org parent = orgMapper.selectById(parentId);
            if (parent == null) {
                throw new IllegalArgumentException("父组织不存在");
            }
        }
        // 深度校验：parent.depth + 1 <= 16；坏链 depth=-1 直接拒绝
        int parentDepth = parentId == BizConstants.ROOT_PARENT_ID ? 0 : computeDepth(parentId);
        if (parentDepth < 0) {
            throw new IllegalArgumentException("父组织链路异常，无法新增");
        }
        if (parentDepth + 1 > BizConstants.MAX_TREE_DEPTH) {
            throw new IllegalArgumentException("超过最大深度 " + BizConstants.MAX_TREE_DEPTH);
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
        entity.setStatus(status == null ? BizConstants.STATUS_ENABLED : status);
        entity.setRemark(dto.getRemark() == null ? null : dto.getRemark().trim());
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        Long userId = currentUserIdOrNull();
        if (userId != null) {
            entity.setCreateId(userId);
            entity.setUpdateId(userId);
        }
        orgMapper.insert(entity);
        return info(entity.getId());
    }

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
            if (dto.getStatus() != BizConstants.STATUS_DISABLED && dto.getStatus() != BizConstants.STATUS_ENABLED) {
                throw new IllegalArgumentException("status 仅支持 0/1");
            }
            existing.setStatus(dto.getStatus());
        }
        if (dto.getRemark() != null) {
            existing.setRemark(dto.getRemark());
        }
        existing.setUpdateTime(LocalDateTime.now());
        Long userId = currentUserIdOrNull();
        if (userId != null) {
            existing.setUpdateId(userId);
        }
        updateById(existing);
        return info(existing.getId());
    }

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
        if (targetParentId != BizConstants.ROOT_PARENT_ID) {
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
        int targetDepth = targetParentId == BizConstants.ROOT_PARENT_ID ? 0 : computeDepth(targetParentId);
        if (targetDepth < 0) {
            throw new IllegalArgumentException("目标父组织链路异常，无法移动");
        }
        int sourceSubtreeHeight = computeSubtreeHeight(id);
        if (targetDepth + sourceSubtreeHeight > BizConstants.MAX_TREE_DEPTH) {
            throw new IllegalArgumentException("超过最大深度 " + BizConstants.MAX_TREE_DEPTH);
        }
        Org upd = new Org();
        upd.setId(id);
        upd.setParentId(targetParentId);
        upd.setUpdateTime(LocalDateTime.now());
        Long userId = currentUserIdOrNull();
        if (userId != null) {
            upd.setUpdateId(userId);
        }
        updateById(upd);
    }

    private boolean isDescendant(Long candidate, Long ancestorId) {
        if (candidate == null || ancestorId == null) {
            return false;
        }
        Set<Long> visited = new HashSet<>();
        Long current = candidate;
        int hops = 0;
        while (current != null && current != BizConstants.ROOT_PARENT_ID) {
            if (!visited.add(current)) {
                return false;
            }
            if (++hops > BizConstants.MAX_TREE_DEPTH + TREE_WALK_SLACK) {
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
        Long userId = currentUserIdOrNull();
        Org upd = new Org();
        upd.setId(id);
        if (userId != null) {
            upd.setDeleteId(userId);
        }
        upd.setDeleteTime(LocalDateTime.now());
        updateById(upd);
        // 触发 @TableLogic → UPDATE ... SET deleted = 1 WHERE id = ? AND deleted = 0
        orgMapper.deleteById(id);
    }

    @Override
    public List<OrgVO> options() {
        List<Org> all = orgMapper.selectEnabledOptions();
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        // 一次性拼 path：按 parent 分桶就地拼
        Map<Long, List<Org>> childrenByParent = new HashMap<>();
        for (Org node : all) {
            childrenByParent.computeIfAbsent(node.getParentId(), ignored -> new ArrayList<>()).add(node);
        }
        Map<Long, String[]> pathById = new HashMap<>();
        List<OrgVO> result = new ArrayList<>(all.size());
        List<Org> roots = childrenByParent.getOrDefault(BizConstants.ROOT_PARENT_ID, Collections.emptyList());
        for (Org root : roots) {
            pathById.put(root.getId(), new String[]{root.getOrgCode(), root.getOrgName()});
            result.add(toVOWithPath(root, root.getOrgCode(), root.getOrgName(), pathById, childrenByParent));
        }
        return result;
    }

    private OrgVO toVOWithPath(Org node,
                               String pathCode,
                               String pathName,
                               Map<Long, String[]> pathById,
                               Map<Long, List<Org>> childrenByParent) {
        OrgVO vo = toFlatVO(node);
        vo.setPathCode(pathCode);
        vo.setPathName(pathName);
        List<Org> childNodes = childrenByParent.getOrDefault(node.getId(), Collections.emptyList());
        vo.setHasChildren(!childNodes.isEmpty());
        // 这里仅返回扁平 + path，前端自己组装树；选择器类型按需
        return vo;
    }

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
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(Role::getRoleCode, roleCode.trim())
                .eq(Role::getDeleted, BizConstants.NOT_DELETED)
                .eq(Role::getStatus, BizConstants.STATUS_ENABLED);
        List<Role> roles = roleMapper.selectList(roleWrapper);
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> roleIds = new ArrayList<>(roles.size());
        for (Role role : roles) {
            roleIds.add(role.getId());
        }
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.in(UserRole::getRoleId, roleIds);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        if (userRoles == null || userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> memberSet = new HashSet<>(memberIds);
        Set<Long> userIds = new HashSet<>();
        for (UserRole userRole : userRoles) {
            if (memberSet.contains(userRole.getUserId())) {
                userIds.add(userRole.getUserId());
            }
        }
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getId, userIds)
                .eq(User::getDeleted, BizConstants.NOT_DELETED)
                .orderByAsc(User::getId);
        return userMapper.selectList(userWrapper);
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
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getId, userIds)
                .eq(User::getDeleted, BizConstants.NOT_DELETED)
                .orderByAsc(User::getId);
        List<User> users = userMapper.selectList(userWrapper);
        List<OrgMemberVO> result = new ArrayList<>();
        if (users == null) {
            return result;
        }
        for (User user : users) {
            OrgMemberVO vo = new OrgMemberVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setStatus(user.getStatus());
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
            for (Long memberUserId : dto.getUserIds()) {
                if (memberUserId != null) {
                    newSet.add(memberUserId);
                }
            }
        }
        if (!newSet.isEmpty()) {
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.in(User::getId, newSet).eq(User::getDeleted, BizConstants.NOT_DELETED);
            Long existed = userMapper.selectCount(userWrapper);
            if (existed == null || existed != newSet.size()) {
                throw new IllegalArgumentException("部分用户不存在或已删除");
            }
        }
        List<Long> subtree = subtreeOrgIds(orgId);
        Set<Long> oldSet = new HashSet<>(userIdsInSubtree(orgId));
        for (Long memberUserId : oldSet) {
            if (!newSet.contains(memberUserId)) {
                userOrgMapper.deleteByUserIdAndOrgIds(memberUserId, subtree);
            }
        }
        for (Long memberUserId : newSet) {
            if (!oldSet.contains(memberUserId)) {
                UserOrg row = new UserOrg();
                row.setUserId(memberUserId);
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
            List<Long> childIds = children.get(id);
            if (childIds != null) {
                stack.addAll(childIds);
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

    private String[] computePathById(Long id) {
        if (id == null) {
            return null;
        }
        List<String[]> segs = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        Long current = id;
        int hops = 0;
        while (current != null && current != BizConstants.ROOT_PARENT_ID) {
            if (!visited.add(current)) {
                return null; // 成环
            }
            if (++hops > BizConstants.MAX_TREE_DEPTH + TREE_WALK_SLACK) {
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
        StringBuilder pathCode = new StringBuilder();
        StringBuilder pathName = new StringBuilder();
        for (int i = 0; i < segs.size(); i++) {
            if (i > 0) {
                pathCode.append('/');
                pathName.append('/');
            }
            pathCode.append(segs.get(i)[0]);
            pathName.append(segs.get(i)[1]);
        }
        return new String[]{pathCode.toString(), pathName.toString()};
    }

    private int computeDepth(Long id) {
        if (id == null || id == BizConstants.ROOT_PARENT_ID) {
            return 0;
        }
        Set<Long> visited = new HashSet<>();
        Long current = id;
        int depth = 0;
        while (current != null && current != BizConstants.ROOT_PARENT_ID) {
            if (!visited.add(current)) {
                return -1;
            }
            if (++depth > BizConstants.MAX_TREE_DEPTH) {
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
            int childHeight = computeSubtreeHeight(child.getId());
            if (childHeight > maxChild) {
                maxChild = childHeight;
            }
        }
        return 1 + maxChild;
    }

    private boolean existsSameCode(Long parentId, String orgCode, Long excludeId) {
        LambdaQueryWrapper<Org> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Org::getParentId, parentId)
                .eq(Org::getOrgCode, orgCode)
                .eq(Org::getDeleted, BizConstants.NOT_DELETED);
        if (excludeId != null) {
            wrapper.ne(Org::getId, excludeId);
        }
        return orgMapper.selectCount(wrapper) > 0;
    }

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
            // 字典还没初始化时映射为空，写入时再校验拒绝
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