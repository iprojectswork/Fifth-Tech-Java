package com.fifthtech.service.dict.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * DictNodeServiceImpl
 *
 * <p>规则（D1–D21）摘要：
 * <ul>
 *   <li>D4/D5：同父未删 code 唯一（DB 部分唯一索引兜底）；code 禁含 /，trim 后非空，
 *   长度 1~64，建议 {@code [A-Za-z0-9_\-\.]+}。</li>
 *   <li>D6：path 不落库，运行时按链拼接；getParent 步数超过 {@link #MAX_DEPTH} 视为坏数据。</li>
 *   <li>D7：列表/树子节点顺序 {@code sort ASC, code ASC, id ASC}。</li>
 *   <li>D8：业务读仅返回 status=1。</li>
 *   <li>D9/D14/D17：删除与 move 要求源无未删子；move 目标必须存在或为 0、id != targetParentId。</li>
 *   <li>D18：最大深度 16（根下第一层 depth=1）。</li>
 *   <li>D21：{@code /dict/data} 按 pathCode 精确匹配走链解析，不存在抛业务异常。</li>
 * </ul>
 * </p>
 *
 * @author RH
 * @description 数据字典节点服务实现
 * @date 2026-08-02
 */
@Service
public class DictNodeServiceImpl extends ServiceImpl<DictNodeMapper, DictNode> implements DictNodeService {

    /** 最大允许深度（C3 D18，根下第一层 depth=1） */
    private static final int MAX_DEPTH = 16;

    /** code 字符合法集（C3 D5：禁 /；trim 后非空；长度 1~64） */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_\\-\\.]+$");

    /** 根节点 parent_id */
    private static final long ROOT_ID = 0L;

    @Resource
    private DictNodeMapper dictNodeMapper;

    // ---------------------------------------------------------------------
    // listChildren / list
    // ---------------------------------------------------------------------

    @Override
    public List<DictNodeVO> listChildren(DictNodeQueryDTO query) {
        return listDirectChildren(query == null ? null : query.getParentId());
    }

    @Override
    public List<DictNodeVO> list(DictNodeQueryDTO query) {
        return listDirectChildren(query == null ? null : query.getParentId());
    }

    private List<DictNodeVO> listDirectChildren(Long parentId) {
        Long pid = parentId == null ? ROOT_ID : parentId;
        List<DictNode> children = dictNodeMapper.selectChildrenByParentId(pid);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }
        // 父 path（仅当 parentId != 0 时走链）
        String[] parentPath = pid == ROOT_ID ? new String[]{"", ""} : computePathById(pid);
        // 批量 hasChildren
        List<Long> childIds = new ArrayList<>(children.size());
        for (DictNode c : children) {
            childIds.add(c.getId());
        }
        Set<Long> hasChildrenSet = new HashSet<>(dictNodeMapper.selectActiveParentIdsWithChildren(childIds));

        List<DictNodeVO> vos = new ArrayList<>(children.size());
        for (DictNode c : children) {
            DictNodeVO vo = toFlatVO(c);
            String pc = parentPath[0];
            String pn = parentPath[1];
            vo.setPathCode(pc.isEmpty() ? c.getCode() : pc + "/" + c.getCode());
            vo.setPathName(pn.isEmpty() ? c.getName() : pn + "/" + c.getName());
            vo.setHasChildren(hasChildrenSet.contains(c.getId()));
            vos.add(vo);
        }
        return vos;
    }

    // ---------------------------------------------------------------------
    // tree
    // ---------------------------------------------------------------------

    @Override
    public List<DictNodeTreeVO> tree() {
        List<DictNode> all = dictNodeMapper.selectActiveList();
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, List<DictNode>> childrenByParent = new HashMap<>();
        for (DictNode n : all) {
            List<DictNode> bucket = childrenByParent.computeIfAbsent(n.getParentId(), k -> new ArrayList<>());
            bucket.add(n);
        }
        List<DictNode> roots = childrenByParent.getOrDefault(ROOT_ID, Collections.emptyList());
        List<DictNodeTreeVO> tree = new ArrayList<>(roots.size());
        for (DictNode r : roots) {
            tree.add(buildTreeNode(r, "", "", childrenByParent));
        }
        return tree;
    }

    private DictNodeTreeVO buildTreeNode(DictNode node,
                                         String parentPathCode,
                                         String parentPathName,
                                         Map<Long, List<DictNode>> childrenByParent) {
        DictNodeTreeVO vo = new DictNodeTreeVO();
        copyFlatFields(node, vo);
        String pc = parentPathCode.isEmpty() ? node.getCode() : parentPathCode + "/" + node.getCode();
        String pn = parentPathName.isEmpty() ? node.getName() : parentPathName + "/" + node.getName();
        vo.setPathCode(pc);
        vo.setPathName(pn);
        List<DictNode> kids = childrenByParent.getOrDefault(node.getId(), Collections.emptyList());
        vo.setHasChildren(!kids.isEmpty());
        for (DictNode k : kids) {
            vo.getChildren().add(buildTreeNode(k, pc, pn, childrenByParent));
        }
        return vo;
    }

    // ---------------------------------------------------------------------
    // info
    // ---------------------------------------------------------------------

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

    // ---------------------------------------------------------------------
    // insert
    // ---------------------------------------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictNodeVO insert(DictNodeDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        String code = normalizeCode(dto.getCode());
        String name = normalizeName(dto.getName());
        Integer status = dto.getStatus();
        if (status != null && status != 0 && status != 1) {
            throw new IllegalArgumentException("status 仅支持 0/1");
        }
        Long parentId = dto.getParentId() == null ? ROOT_ID : dto.getParentId();
        if (parentId != ROOT_ID) {
            DictNode parent = dictNodeMapper.selectById(parentId);
            if (parent == null) {
                throw new IllegalArgumentException("父节点不存在");
            }
        }
        // 深度校验：parent.depth + 1 <= 16；坏链 depth=-1 直接拒绝
        int parentDepth = parentId == ROOT_ID ? 0 : computeDepth(parentId);
        if (parentDepth < 0) {
            throw new IllegalArgumentException("父节点链路异常，无法新增");
        }
        if (parentDepth + 1 > MAX_DEPTH) {
            throw new IllegalArgumentException("超过最大深度 " + MAX_DEPTH);
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
        dictNodeMapper.insert(entity);
        return info(entity.getId());
    }

    // ---------------------------------------------------------------------
    // edit
    // ---------------------------------------------------------------------

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
        List<Long> sourceKids = dictNodeMapper.selectActiveParentIdsWithChildren(Collections.singletonList(id));
        if (sourceKids != null && !sourceKids.isEmpty()) {
            throw new IllegalArgumentException("存在子节点，不允许移动");
        }
        // 目标存在或为 0
        if (targetParentId != ROOT_ID) {
            DictNode tp = dictNodeMapper.selectById(targetParentId);
            if (tp == null) {
                throw new IllegalArgumentException("目标父节点不存在");
            }
        }
        // 新父下 code 唯一（排除自身）
        if (existsSameCode(targetParentId, source.getCode(), id)) {
            throw new IllegalArgumentException("目标父下 code 已存在");
        }
        // 深度校验
        int targetDepth = targetParentId == ROOT_ID ? 0 : computeDepth(targetParentId);
        if (targetDepth < 0) {
            throw new IllegalArgumentException("目标父节点链路异常，无法移动");
        }
        if (targetDepth + 1 > MAX_DEPTH) {
            throw new IllegalArgumentException("超过最大深度 " + MAX_DEPTH);
        }
        DictNode upd = new DictNode();
        upd.setId(id);
        upd.setParentId(targetParentId);
        upd.setUpdateTime(LocalDateTime.now());
        Long uid = currentUserIdOrNull();
        if (uid != null) {
            upd.setUpdateId(uid);
        }
        updateById(upd);
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
        DictNode existing = dictNodeMapper.selectById(id);
        if (existing == null) {
            return;
        }
        List<Long> kids = dictNodeMapper.selectActiveParentIdsWithChildren(Collections.singletonList(id));
        if (kids != null && !kids.isEmpty()) {
            throw new IllegalArgumentException("存在子节点，不允许删除");
        }
        Long uid = currentUserIdOrNull();
        DictNode upd = new DictNode();
        upd.setId(id);
        if (uid != null) {
            upd.setDeleteId(uid);
        }
        upd.setDeleteTime(LocalDateTime.now());
        updateById(upd);
        // 触发 @TableLogic → UPDATE ... SET deleted = 1 WHERE id = ? AND deleted = 0
        dictNodeMapper.deleteById(id);
    }

    // ---------------------------------------------------------------------
    // listDataByPathCode
    // ---------------------------------------------------------------------

    @Override
    public List<DictNodeVO> listDataByPathCode(DictNodeQueryDTO query) {
        String pathCode = query == null ? null : query.getPathCode();
        if (pathCode == null || pathCode.isEmpty()) {
            throw new IllegalArgumentException("pathCode 不能为空");
        }
        String[] parts = pathCode.split("/");
        // 沿 parent 链匹配 code 段；同步累积 (code, name) 以便后续拼 child.path
        Long parentId = ROOT_ID;
        List<String[]> chain = new ArrayList<>(parts.length);
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            LambdaQueryWrapper<DictNode> w = new LambdaQueryWrapper<>();
            w.eq(DictNode::getParentId, parentId)
                    .eq(DictNode::getCode, part)
                    .eq(DictNode::getDeleted, 0);
            DictNode node = dictNodeMapper.selectOne(w);
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
        StringBuilder parentPc = new StringBuilder();
        StringBuilder parentPn = new StringBuilder();
        for (int i = 0; i < chain.size(); i++) {
            if (i > 0) {
                parentPc.append('/');
                parentPn.append('/');
            }
            parentPc.append(chain.get(i)[0]);
            parentPn.append(chain.get(i)[1]);
        }
        List<DictNode> children = dictNodeMapper.selectChildrenByParentId(targetId);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }
        // 只返回 status=1 的直接子
        List<DictNodeVO> vos = new ArrayList<>(children.size());
        List<Long> childIds = new ArrayList<>(children.size());
        for (DictNode c : children) {
            if (c.getStatus() == null || c.getStatus() != 1) {
                continue;
            }
            DictNodeVO vo = toFlatVO(c);
            vo.setPathCode(parentPc.toString() + "/" + c.getCode());
            vo.setPathName(parentPn.toString() + "/" + c.getName());
            childIds.add(c.getId());
            vos.add(vo);
        }
        if (!vos.isEmpty()) {
            Set<Long> hasKids = new HashSet<>(dictNodeMapper.selectActiveParentIdsWithChildren(childIds));
            for (DictNodeVO vo : vos) {
                vo.setHasChildren(hasKids.contains(vo.getId()));
            }
        }
        return vos;
    }

    // ---------------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------------

    /**
     * 走 parent 链拼 pathCode / pathName（不超过 MAX_DEPTH 步数；步数超限或成环返回 null）。
     * 用于 info 与 listDataByPathCode 的兜底；tree 走内存构建不走这里。
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
            DictNode node = dictNodeMapper.selectById(current);
            if (node == null) {
                return -1;
            }
            current = node.getParentId();
        }
        return depth;
    }

    /**
     * 同 parent 下未删 code 唯一性校验（{@code excludeId} 可选：edit 时排除自身）。
     */
    private boolean existsSameCode(Long parentId, String code, Long excludeId) {
        LambdaQueryWrapper<DictNode> w = new LambdaQueryWrapper<>();
        w.eq(DictNode::getParentId, parentId)
                .eq(DictNode::getCode, code)
                .eq(DictNode::getDeleted, 0);
        if (excludeId != null) {
            w.ne(DictNode::getId, excludeId);
        }
        return dictNodeMapper.selectCount(w) > 0;
    }

    /**
     * 校验并返回 trim 后的 code（C3 D5）；调用方必须用返回值落库。
     */
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

    /**
     * 校验并返回 trim 后的 name；禁止空名（DB NOT NULL）。
     */
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