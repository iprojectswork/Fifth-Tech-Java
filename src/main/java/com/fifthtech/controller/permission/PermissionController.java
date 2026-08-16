package com.fifthtech.controller.permission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.common.Result;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.dto.permission.PermissionDTO;
import com.fifthtech.dto.permission.PermissionQueryDTO;
import com.fifthtech.service.permission.PermissionService;
import com.fifthtech.vo.permission.PermissionTreeVO;
import com.fifthtech.vo.permission.PermissionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author RH
 * @ClassName PermissionController
 * @description: 权限控制器
 * @date 2026年03月22日
 * @version: 1.0
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
    * @description: 分页查询权限列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link Page}<{@link PermissionVO}>>
    **/
    @GetMapping("/list")
    public Result<Page<PermissionVO>> list(PermissionQueryDTO query) {
        Page<Permission> entityPage = permissionService.selectPage(query);
        Page<PermissionVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(ConvertUtils.toVOList(entityPage.getRecords(), PermissionVO.class));
        return Result.success("查询成功", voPage);
    }

    /**
    * @description: 查询权限树
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link Result}<{@link List}<{@link PermissionTreeVO}>>
    **/
    @GetMapping("/tree")
    public Result<List<PermissionTreeVO>> tree() {
        List<PermissionTreeVO> tree = permissionService.selectTree();
        return Result.success("查询成功", tree);
    }

    /**
    * @description: 根据父id查询子权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link List}<{@link PermissionVO}>>
    **/
    @GetMapping("/children")
    public Result<List<PermissionVO>> children(PermissionQueryDTO query) {
        if (query == null) {
            query = new PermissionQueryDTO();
        }
        if (query.getParentId() == null) {
            query.setParentId(0L);
        }
        return Result.success("查询成功", permissionService.listChildren(query));
    }

    /**
    * @description: 根据id查询权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Result}<{@link PermissionVO}>
    **/
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
    * @description: 新增权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link PermissionVO}>
    **/
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
    * @description: 根据id修改权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link PermissionVO}>
    **/
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
    * @description: 根据id删除权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Result}<{@link Void}>
    **/
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