package com.fifthtech.controller.role;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.common.Result;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dto.role.RoleDTO;
import com.fifthtech.service.role.RoleService;
import com.fifthtech.vo.role.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * RoleController
 *
 * <p>角色 API。角色是全局权限包，不挂组织。</p>
 *
 * @author RH
 * @description 角色控制器
 * @date 2026-03-22
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 角色分页列表
     */
    @GetMapping("/list")
    public Result<Page<RoleVO>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleCode) {
        Page<Role> entityPage = roleService.selectPage(current, size, roleName, roleCode);
        Page<RoleVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(ConvertUtils.toVOList(entityPage.getRecords(), RoleVO.class));
        return Result.success("查询成功", voPage);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    public Result<RoleVO> getById(@PathVariable Long id) {
        if (id == null) {
            return Result.error("id 不能为空");
        }
        Role entity = roleService.selectById(id);
        if (entity != null) {
            RoleVO vo = ConvertUtils.toVO(entity, RoleVO.class);
            return Result.success("查询成功", vo);
        }
        return Result.error("角色不存在");
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result<RoleVO> insert(@RequestBody RoleDTO dto) {
        try {
            Role entity = roleService.insert(dto);
            RoleVO vo = ConvertUtils.toVO(entity, RoleVO.class);
            return Result.success("添加成功", vo);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 更新角色
     */
    @PutMapping
    public Result<RoleVO> update(@RequestBody RoleDTO dto) {
        if (dto == null || dto.getId() == null) {
            return Result.error("角色ID不能为空");
        }
        try {
            Role entity = roleService.update(dto);
            if (entity == null) {
                return Result.error("角色不存在");
            }
            RoleVO vo = ConvertUtils.toVO(entity, RoleVO.class);
            return Result.success("更新成功", vo);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (id == null) {
            return Result.error("id 不能为空");
        }
        try {
            roleService.deleteById(id);
            return Result.success("删除成功", null);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 获取所有启用角色（下拉选择用）
     */
    @GetMapping("/all")
    public Result<List<RoleVO>> getAll() {
        List<Role> roles = roleService.selectAll();
        List<RoleVO> voList = ConvertUtils.toVOList(roles == null ? new ArrayList<>() : roles, RoleVO.class);
        return Result.success("查询成功", voList);
    }

    /**
     * 获取角色的权限ID列表
     */
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissions(@PathVariable Long id) {
        if (id == null) {
            return Result.error("id 不能为空");
        }
        List<Long> permissionIds = roleService.getPermissionIdsByRoleId(id);
        return Result.success("查询成功", permissionIds);
    }

    /**
     * 分配角色权限
     */
    @PutMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        if (id == null) {
            return Result.error("id 不能为空");
        }
        try {
            roleService.assignPermissions(id, permissionIds);
            return Result.success("分配成功", null);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }
}
