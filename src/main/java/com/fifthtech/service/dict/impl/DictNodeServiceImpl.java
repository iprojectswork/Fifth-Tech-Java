package com.fifthtech.service.dict.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fifthtech.common.BizConstants;
import com.fifthtech.dao.entity.dict.DictNode;
import com.fifthtech.dao.mapper.dict.DictNodeMapper;
import com.fifthtech.dto.dict.DictNodeDTO;
import com.fifthtech.dto.dict.DictNodeMoveDTO;
import com.fifthtech.dto.dict.DictNodeQueryDTO;
import com.fifthtech.security.UserContext;
import com.fifthtech.service.dict.DictNodeService;
import com.fifthtech.vo.dict.DictNodeTreeVO;
import com.fifthtech.vo.dict.DictNodeVO;
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
 * @ClassName DictNodeServiceImpl
 * @description: 数据字典节点服务实现
 * @date 2026年08月02日
 * @version: 1.0
 */
@Service
public class DictNodeServiceImpl extends ServiceImpl<DictNodeMapper, DictNode> implements DictNodeService {

    /**
     * 沿 parent 链多走几步，用于识别坏链
     */
    private static final int TREE_WALK_SLACK = 4;

    /**
     * code 字符合法集（禁 /；trim 后非空；长度 1~64）
     */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_\\-\\.]+$");

    @Resource
    private DictNodeMapper dictNodeMapper;

    @Override
    public List<DictNodeVO> listChildren(DictNodeQueryDTO query) {
        return listDirectChildren(query == null ? null : query.getParentId());
    }

    @Override
    public List<DictNodeVO> list(DictNodeQueryDTO query) {
        return listDirectChildren(query == null ? null : query.getParentId());
    }

    private List<DictNodeVO> listDirectChildren(Long parentId) {
        Long resolvedParentId = parentId == null ? BizConstants.ROOT_PARENT_ID : parentId;
        List<DictNode> children = dictNodeMapper.selectChildrenByParentId(resolvedParentId);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }
        String[] parentPath = resolvedParentId == BizConstants.ROOT_PARENT_ID
                ? new String[]{"", ""} : computePathById(resolvedParentId);
        List<Long> childIds = new ArrayList<>(children.size());
        for (DictNode child : children) {
            childIds.add(child.getId());
        }
        Set<Long> hasChildrenSet = new HashSet<>(dictNodeMapper.selectActiveParentIdsWithChildren(childIds));

        List<DictNodeVO> vos = new ArrayList<>(children.size());
        for (DictNode child : children) {
            DictNodeVO vo = toFlatVO(child);
            String pathCode = parentPath[0];
            String pathName = parentPath[1];
            vo.setPathCode(pathCode.isEmpty() ? child.getCode() : pathCode + "/" + child.getCode());
            vo.setPathName(pathName.isEmpty() ? child.getName() : pathName + "/" + child.getName());
            vo.setHasChildren(hasChildrenSet.contains(child.getId()));
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<DictNodeTreeVO> tree() {
        List<DictNode> all = dictNodeMapper.selectActiveList();
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, List<DictNode>> childrenByParent = new HashMap<>();
        for (DictNode node : all) {
            List<DictNode> bucket = childrenByParent.computeIfAbsent(node.getParentId(), ignored -> new ArrayList<>());
            bucket.add(node);
        }
        List<DictNode> roots = childrenByParent.getOrDefault(BizConstants.ROOT_PARENT_ID, Collections.emptyList());
        List<DictNodeTreeVO> tree = new ArrayList<>(roots.size());
        for (DictNode root : roots) {
            tree.add(buildTreeNode(root, "", "", childrenByParent));
        }
        return tree;
    }

    private DictNodeTreeVO buildTreeNode(DictNode node,
                                         String parentPathCode,
                                         String parentPathName,
                                         Map<Long, List<DictNode>> childrenByParent) {
        DictNodeTreeVO vo = new DictNodeTreeVO();
        copyFlatFields(node, vo);
        String pathCode = parentPathCode.isEmpty() ? node.getCode() : parentPathCode + "/" + node.getCode();
        String pathName = parentPathName.isEmpty() ? node.getName() : parentPathName + "/" + node.getName();
        vo.setPathCode(pathCode);
        vo.setPathName(pathName);
        List<DictNode> childNodes = childrenByParent.getOrDefault(node.getId(), Collections.emptyList());
        vo.setHasChildren(!childNodes.isEmpty());
        for (DictNode child : childNodes) {
            vo.getChildren().add(buildTreeNode(child, pathCode, pathName, childrenByParent));
        }
        return vo;
    }

    @Override
    public DictNodeVO info(Long id) {
        if (id == null) {
            return null;
        }
        DictNode node = dictNodeMapper.selectById(id);
        if (node == null) {
            return null;
        }
        DictNodeVO vo = toFlatVO(node);
        String[] path = computePathById(id);
        if (path != null) {
            vo.setPathCode(path[0]);
            vo.setPathName(path[1]);
        } else {
            // 兜底：走链失败则至少填自身
            vo.setPathCode(node.getCode());
            vo.setPathName(node.getName());
        }
        Set<Long> hasKids = new HashSet<>(
                dictNodeMapper.selectActiveParentIdsWithChildren(Collections.singletonList(id)));
        vo.setHasChildren(hasKids.contains(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictNodeVO insert(DictNodeDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        String code = normalizeCode(dto.getCode());
        String name = normalizeName(dto.getName());
        Integer status = dto.getStatus();
        if (status != null && status != BizConstants.STATUS_DISABLED && status != BizConstants.STATUS_ENABLED) {
            throw new IllegalArgumentException("status 仅支持 0/1");
        }
        Long parentId = dto.getParentId() == null ? BizConstants.ROOT_PARENT_ID : dto.getParentId();
        if (parentId != BizConstants.ROOT_PARENT_ID) {
            DictNode parent = dictNodeMapper.selectById(parentId);
            if (parent == null) {
                throw new IllegalArgumentException("父节点不存在");
            }
        }
        // 深度校验：parent.depth + 1 <= 16；坏链 depth=-1 直接拒绝
        int parentDepth = parentId == BizConstants.ROOT_PARENT_ID ? 0 : computeDepth(parentId);
        if (parentDepth < 0) {
            throw new IllegalArgumentException("父节点链路异常，无法新增");
        }
        if (parentDepth + 1 > BizConstants.MAX_TREE_DEPTH) {
            throw new IllegalArgumentException("超过最大深度 " + BizConstants.MAX_TREE_DEPTH);
        }
        // 同父 code 唯一（DB 部分唯一索引兜底）
        if (existsSameCode(parentId, code, null)) {
            throw new IllegalArgumentException("同父下 code 已存在");
        }
        DictNode entity = new DictNode();
        entity.setParentId(parentId);
        entity.setCode(code);
        entity.setName(name);
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
        dictNodeMapper.insert(entity);
        return info(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictNodeVO edit(DictNodeDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        DictNode existing = dictNodeMapper.selectById(dto.getId());
        if (existing == null) {
            throw new IllegalArgumentException("节点不存在");
        }
        // code 改了才校验（忽略 parentId，不允许通过 edit 改挂）；落库使用 trim 后值
        if (dto.getCode() != null) {
            String code = normalizeCode(dto.getCode());
            if (!code.equals(existing.getCode())) {
                if (existsSameCode(existing.getParentId(), code, existing.getId())) {
                    throw new IllegalArgumentException("同父下 code 已存在");
                }
                existing.setCode(code);
            }
        }
        if (dto.getName() != null) {
            existing.setName(normalizeName(dto.getName()));
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
    public void move(DictNodeMoveDTO dto) {
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
        DictNode source = dictNodeMapper.selectById(id);
        if (source == null) {
            throw new IllegalArgumentException("节点不存在");
        }
        // 源必须无未删子
        List<Long> sourceChildParentIds = dictNodeMapper.selectActiveParentIdsWithChildren(Collections.singletonList(id));
        if (sourceChildParentIds != null && !sourceChildParentIds.isEmpty()) {
            throw new IllegalArgumentException("存在子节点，不允许移动");
        }
        if (targetParentId != BizConstants.ROOT_PARENT_ID) {
            DictNode targetParent = dictNodeMapper.selectById(targetParentId);
            if (targetParent == null) {
                throw new IllegalArgumentException("目标父节点不存在");
            }
        }
        // 新父下 code 唯一（排除自身）
        if (existsSameCode(targetParentId, source.getCode(), id)) {
            throw new IllegalArgumentException("目标父下 code 已存在");
        }
        // 深度校验
        int targetDepth = targetParentId == BizConstants.ROOT_PARENT_ID ? 0 : computeDepth(targetParentId);
        if (targetDepth < 0) {
            throw new IllegalArgumentException("目标父节点链路异常，无法移动");
        }
        if (targetDepth + 1 > BizConstants.MAX_TREE_DEPTH) {
            throw new IllegalArgumentException("超过最大深度 " + BizConstants.MAX_TREE_DEPTH);
        }
        DictNode upd = new DictNode();
        upd.setId(id);
        upd.setParentId(targetParentId);
        upd.setUpdateTime(LocalDateTime.now());
        Long userId = currentUserIdOrNull();
        if (userId != null) {
            upd.setUpdateId(userId);
        }
        updateById(upd);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        DictNode existing = dictNodeMapper.selectById(id);
        if (existing == null) {
            return;
        }
        List<Long> childParentIds = dictNodeMapper.selectActiveParentIdsWithChildren(Collections.singletonList(id));
        if (childParentIds != null && !childParentIds.isEmpty()) {
            throw new IllegalArgumentException("存在子节点，不允许删除");
        }
        Long userId = currentUserIdOrNull();
        DictNode upd = new DictNode();
        upd.setId(id);
        if (userId != null) {
            upd.setDeleteId(userId);
        }
        upd.setDeleteTime(LocalDateTime.now());
        updateById(upd);
        // 触发 @TableLogic → UPDATE ... SET deleted = 1 WHERE id = ? AND deleted = 0
        dictNodeMapper.deleteById(id);
    }

    @Override
    public List<DictNodeVO> listDataByPathCode(DictNodeQueryDTO query) {
        String pathCode = query == null ? null : query.getPathCode();
        if (pathCode == null || pathCode.isEmpty()) {
            throw new IllegalArgumentException("pathCode 不能为空");
        }
        String[] parts = pathCode.split("/");
        // 沿 parent 链匹配 code 段；同步累积 (code, name) 以便后续拼 child.path
        Long parentId = BizConstants.ROOT_PARENT_ID;
        List<String[]> chain = new ArrayList<>(parts.length);
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            LambdaQueryWrapper<DictNode> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictNode::getParentId, parentId)
                    .eq(DictNode::getCode, part)
                    .eq(DictNode::getDeleted, BizConstants.NOT_DELETED);
            DictNode node = dictNodeMapper.selectOne(wrapper);
            if (node == null) {
                throw new IllegalArgumentException("字典路径不存在");
            }
            chain.add(new String[]{node.getCode(), node.getName()});
            parentId = node.getId();
        }
        if (chain.isEmpty()) {
            throw new IllegalArgumentException("字典路径不存在");
        }
        Long targetId = parentId;
        StringBuilder parentPathCode = new StringBuilder();
        StringBuilder parentPathName = new StringBuilder();
        for (int i = 0; i < chain.size(); i++) {
            if (i > 0) {
                parentPathCode.append('/');
                parentPathName.append('/');
            }
            parentPathCode.append(chain.get(i)[0]);
            parentPathName.append(chain.get(i)[1]);
        }
        List<DictNode> children = dictNodeMapper.selectChildrenByParentId(targetId);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }
        List<DictNodeVO> vos = new ArrayList<>(children.size());
        List<Long> childIds = new ArrayList<>(children.size());
        for (DictNode child : children) {
            if (child.getStatus() == null || child.getStatus() != BizConstants.STATUS_ENABLED) {
                continue;
            }
            DictNodeVO vo = toFlatVO(child);
            vo.setPathCode(parentPathCode.toString() + "/" + child.getCode());
            vo.setPathName(parentPathName.toString() + "/" + child.getName());
            childIds.add(child.getId());
            vos.add(vo);
        }
        if (!vos.isEmpty()) {
            Set<Long> parentsWithChildren = new HashSet<>(dictNodeMapper.selectActiveParentIdsWithChildren(childIds));
            for (DictNodeVO vo : vos) {
                vo.setHasChildren(parentsWithChildren.contains(vo.getId()));
            }
        }
        return vos;
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
            DictNode node = dictNodeMapper.selectById(current);
            if (node == null) {
                return null;
            }
            segs.add(0, new String[]{node.getCode(), node.getName()});
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
            DictNode node = dictNodeMapper.selectById(current);
            if (node == null) {
                return -1;
            }
            current = node.getParentId();
        }
        return depth;
    }

    private boolean existsSameCode(Long parentId, String code, Long excludeId) {
        LambdaQueryWrapper<DictNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictNode::getParentId, parentId)
                .eq(DictNode::getCode, code)
                .eq(DictNode::getDeleted, BizConstants.NOT_DELETED);
        if (excludeId != null) {
            wrapper.ne(DictNode::getId, excludeId);
        }
        return dictNodeMapper.selectCount(wrapper) > 0;
    }

    private String normalizeCode(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("code 不能为空");
        }
        String code = raw.trim();
        if (code.isEmpty()) {
            throw new IllegalArgumentException("code 不能为空");
        }
        if (code.length() > 64) {
            throw new IllegalArgumentException("code 长度不能超过 64");
        }
        if (code.indexOf('/') >= 0) {
            throw new IllegalArgumentException("code 不能包含 /");
        }
        if (!CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("code 仅支持字母、数字、下划线、连字符、点");
        }
        return code;
    }

    private String normalizeName(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("name 不能为空");
        }
        String name = raw.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name 不能为空");
        }
        if (name.length() > 128) {
            throw new IllegalArgumentException("name 长度不能超过 128");
        }
        return name;
    }

    private DictNodeVO toFlatVO(DictNode n) {
        DictNodeVO vo = new DictNodeVO();
        copyFlatFields(n, vo);
        return vo;
    }

    private void copyFlatFields(DictNode n, DictNodeVO vo) {
        vo.setId(n.getId());
        vo.setParentId(n.getParentId());
        vo.setCode(n.getCode());
        vo.setName(n.getName());
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

    private Long currentUserIdOrNull() {
        try {
            return UserContext.getCurrentUserId();
        } catch (RuntimeException ignore) {
            return null;
        }
    }
}