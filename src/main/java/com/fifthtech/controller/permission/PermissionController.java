package com.fifthtech.controller.permission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.common.Result;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.dto.permission.PermissionDTO;
import com.fifthtech.service.permission.PermissionService;
import com.fifthtech.vo.permission.PermissionTreeVO;
import com.fifthtech.vo.permission.PermissionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PermissionController
 *
 * @author RH
 * @description 权限控制器
 * @date 2026-03-22
 * @version 1.0
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 权限分页列表
     */
    @GetMapping("/list")
    public Result<Page<PermissionVO>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) String permissionCode) {
        Page<Permission> entityPage = permissionService.selectPage(current, size, permissionName, permissionCode);
        Page<PermissionVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(ConvertUtils.toVOList(entityPage.getRecords(), PermissionVO.class));
        return Result.success("查询成功", voPage);
    }

    /**
     * 权限树结构（用于分配权限）
     */
    @GetMapping("/tree")
    public Result<List<PermissionTreeVO>> tree() {
        List<PermissionTreeVO> tree = permissionService.selectTree();
        return Result.success("查询成功", tree);
    }

    /**
     * 获取权限详情
     */
    @GetMapping("/{id}")
    public Result<PermissionVO> getById(@PathVariable Long id) {
        Permission entity = permissionService.selectById(id);
        if (entity != null) {
            PermissionVO vo = ConvertUtils.toVO(entity, PermissionVO.class);
            return Result.success("查询成功", vo);
        } else {
            return Result.error("权限不存在");
        }
    }

    /**
     * 创建权限
     */
    @PostMapping
    public Result<PermissionVO> insert(@RequestBody PermissionDTO dto) {
        // 检查权限编码是否已存在
        Permission existPermission = permissionService.lambdaQuery()
                .eq(Permission::getPermissionCode, dto.getPermissionCode())
                .one();
        if (existPermission != null) {
            return Result.error("权限编码已存在");
        }
        Permission entity = permissionService.insert(dto);
        PermissionVO vo = ConvertUtils.toVO(entity, PermissionVO.class);
        return Result.success("添加成功", vo);
    }

    /**
     * 更新权限
     */
    @PutMapping
    public Result<PermissionVO> update(@RequestBody PermissionDTO dto) {
        if (dto.getId() == null) {
            return Result.error("权限ID不能为空");
        }
        Permission existPermission = permissionService.selectById(dto.getId());
        if (existPermission == null) {
            return Result.error("权限不存在");
        }
        // 检查权限编码是否被其他权限使用
        Permission codePermission = permissionService.lambdaQuery()
                .eq(Permission::getPermissionCode, dto.getPermissionCode())
                .ne(Permission::getId, dto.getId())
                .one();
        if (codePermission != null) {
            return Result.error("权限编码已被使用");
        }
        Permission entity = permissionService.update(dto);
        PermissionVO vo = ConvertUtils.toVO(entity, PermissionVO.class);
        return Result.success("更新成功", vo);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Permission existPermission = permissionService.selectById(id);
        if (existPermission == null) {
            return Result.error("权限不存在");
        }
        // 检查是否有子权限
        long childCount = permissionService.lambdaQuery()
                .eq(Permission::getParentId, id)
                .count();
        if (childCount > 0) {
            return Result.error("存在子权限，无法删除");
        }
        permissionService.deleteById(id);
        return Result.success("删除成功", null);
    }
}
