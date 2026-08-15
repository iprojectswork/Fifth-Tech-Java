package com.fifthtech.controller.org;

import com.fifthtech.common.Result;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dto.org.OrgDTO;
import com.fifthtech.dto.org.OrgMembersDTO;
import com.fifthtech.dto.org.OrgMoveDTO;
import com.fifthtech.dto.org.OrgQueryDTO;
import com.fifthtech.service.org.OrgService;
import com.fifthtech.vo.org.OrgMemberVO;
import com.fifthtech.vo.org.OrgTreeVO;
import com.fifthtech.vo.org.OrgVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * OrgController
 *
 * <p>组织 API（C4 §7.2）。仅 Token 登录拦截，不做 L3 权限码校验。
 * 写操作（insert/edit/delete/move）业务校验与事务均在 Service 层。</p>
 *
 * @author RH
 * @description 组织控制器
 * @date 2026-08-09
 */
@RestController
@RequestMapping("/org")
public class OrgController {

    @Resource
    private OrgService orgService;

    /**
     * 懒加载 children：左树展开节点时调用
     */
    @GetMapping("/children")
    public Result<List<OrgVO>> listChildren(OrgQueryDTO query) {
        if (query == null) {
            query = new OrgQueryDTO();
        }
        if (query.getParentId() == null) {
            query.setParentId(0L);
        }
        return Result.success("查询成功", orgService.listChildren(query));
    }

    /**
     * 右栏 list（与 children 同数据源，方法别名）
     */
    @GetMapping("/list")
    public Result<List<OrgVO>> list(OrgQueryDTO query) {
        if (query == null) {
            query = new OrgQueryDTO();
        }
        if (query.getParentId() == null) {
            query.setParentId(0L);
        }
        return Result.success("查询成功", orgService.list(query));
    }

    /**
     * 全量树（含叶子与 path）
     */
    @GetMapping("/tree")
    public Result<List<OrgTreeVO>> tree() {
        return Result.success("查询成功", orgService.tree());
    }

    /**
     * 详情
     */
    @GetMapping("/info-by-id/{id}")
    public Result<OrgVO> info(@PathVariable Long id) {
        OrgVO vo = orgService.info(id);
        if (vo == null) {
            return Result.error("组织不存在");
        }
        return Result.success("查询成功", vo);
    }

    /**
     * 新增组织
     */
    @PostMapping
    public Result<OrgVO> insert(@RequestBody OrgDTO dto) {
        if (dto == null) {
            return Result.error("请求体不能为空");
        }
        try {
            return Result.success("添加成功", orgService.insert(dto));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 修改组织（不允许通过本接口改 parent；改挂请走 /org/move）
     */
    @PutMapping
    public Result<OrgVO> edit(@RequestBody OrgDTO dto) {
        if (dto == null || dto.getId() == null) {
            return Result.error("id 不能为空");
        }
        try {
            return Result.success("更新成功", orgService.edit(dto));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 改挂父组织（整树可拖；服务端权威校验；防环 + 深度 ≤ 16）
     */
    @PutMapping("/move")
    public Result<Void> move(@RequestBody OrgMoveDTO dto) {
        if (dto == null) {
            return Result.error("请求体不能为空");
        }
        try {
            orgService.move(dto);
            return Result.success("移动成功", null);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 逻辑删除（有未删子 / 成员 → 后端拒绝）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (id == null) {
            return Result.error("id 不能为空");
        }
        try {
            orgService.delete(id);
            return Result.success("删除成功", null);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 选择器：仅启用组织全量扁平列表（前端组装树用）
     */
    @GetMapping("/options")
    public Result<List<OrgVO>> options() {
        return Result.success("查询成功", orgService.options());
    }

    /**
     * 查人：组织成员 ∩ 持有该全局角色的用户
     */
    @GetMapping("/users-by-role")
    public Result<List<User>> usersByRole(OrgQueryDTO query) {
        try {
            return Result.success("查询成功", orgService.usersByRole(query));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 本组织成员
     */
    @GetMapping("/members")
    public Result<List<OrgMemberVO>> listMembers(OrgQueryDTO query) {
        try {
            return Result.success("查询成功", orgService.listMembers(query));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 全量替换本组织成员
     */
    @PutMapping("/members")
    public Result<Void> replaceMembers(@RequestBody OrgMembersDTO dto) {
        if (dto == null || dto.getOrgId() == null) {
            return Result.error("orgId 不能为空");
        }
        try {
            orgService.replaceMembers(dto);
            return Result.success("保存成功", null);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }
}