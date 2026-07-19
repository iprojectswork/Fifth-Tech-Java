package com.fifthtech.controller.role;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.common.Result;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dto.role.RoleDTO;
import com.fifthtech.service.role.RoleService;
import com.fifthtech.vo.role.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RoleController
 *
 * @author RH
 * @description 角色控制器
 * @date 2026-03-22
 * @version 1.0
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
        Role entity = roleService.selectById(id);
        if (entity != null) {
            RoleVO vo = ConvertUtils.toVO(entity, RoleVO.class);
            return Result.success("查询成功", vo);
        } else {
            return Result.error("角色不存在");
        }
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result<RoleVO> insert(@RequestBody RoleDTO dto) {
        // 检查角色编码是否已存在
        Role existRole = roleService.lambdaQuery()
                .eq(Role::getRoleCode, dto.getRoleCode())
                .one();
        if (existRole != null) {
            return Result.error("角色编码已存在");
        }
        Role entity = roleService.insert(dto);
        RoleVO vo = ConvertUtils.toVO(entity, RoleVO.class);
        return Result.success("添加成功", vo);
    }

    /**
     * 更新角色
     */
    @PutMapping
    public Result<RoleVO> update(@RequestBody RoleDTO dto) {
        if (dto.getId() == null) {
            return Result.error("角色ID不能为空");
        }
        Role existRole = roleService.selectById(dto.getId());
        if (existRole == null) {
            return Result.error("角色不存在");
        }
        // 检查角色编码是否被其他角色使用
        Role codeRole = roleService.lambdaQuery()
                .eq(Role::getRoleCode, dto.getRoleCode())
                .ne(Role::getId, dto.getId())
                .one();
        if (codeRole != null) {
            return Result.error("角色编码已被使用");
        }
        Role entity = roleService.update(dto);
        RoleVO vo = ConvertUtils.toVO(entity, RoleVO.class);
        return Result.success("更新成功", vo);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Role existRole = roleService.selectById(id);
        if (existRole == null) {
            return Result.error("角色不存在");
        }
        roleService.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取所有角色（下拉选择用）
     */
    @GetMapping("/all")
    public Result<List<RoleVO>> getAll() {
        List<Role> roles = roleService.selectAll();
        List<RoleVO> voList = ConvertUtils.toVOList(roles, RoleVO.class);
        return Result.success("查询成功", voList);
    }

    /**
     * 获取角色的权限ID列表
     */
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissions(@PathVariable Long id) {
        List<Long> permissionIds = roleService.getPermissionIdsByRoleId(id);
        return Result.success("查询成功", permissionIds);
    }

    /**
     * 分配角色权限
     */
    @PutMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        Role existRole = roleService.selectById(id);
        if (existRole == null) {
            return Result.error("角色不存在");
        }
        roleService.assignPermissions(id, permissionIds);
        return Result.success("分配成功", null);
    }
}
