package com.fifthtech.controller.role;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.common.Result;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dto.role.RoleDTO;
import com.fifthtech.dto.role.RoleQueryDTO;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RH
 * @ClassName RoleController
 * @description: 角色控制器
 * @date 2026年03月22日
 * @version: 1.0
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
    * @description: 分页查询角色列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link Page}<{@link RoleVO}>>
    **/
    @GetMapping("/list")
    public Result<Page<RoleVO>> list(RoleQueryDTO query) {
        Page<Role> entityPage = roleService.selectPage(query);
        Page<RoleVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(ConvertUtils.toVOList(entityPage.getRecords(), RoleVO.class));
        return Result.success("查询成功", voPage);
    }

    /**
    * @description: 根据id查询角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Result}<{@link RoleVO}>
    **/
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
    * @description: 新增角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link RoleVO}>
    **/
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
    * @description: 根据id修改角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link RoleVO}>
    **/
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
    * @description: 根据id删除角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Result}<{@link Void}>
    **/
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
    * @description: 查询启用角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link Result}<{@link List}<{@link RoleVO}>>
    **/
    @GetMapping("/all")
    public Result<List<RoleVO>> getAll() {
        List<Role> roles = roleService.selectAll();
        List<RoleVO> voList = ConvertUtils.toVOList(roles == null ? new ArrayList<>() : roles, RoleVO.class);
        return Result.success("查询成功", voList);
    }

    /**
    * @description: 根据角色id查询权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Result}<{@link List}<{@link Long}>>
    **/
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissions(@PathVariable Long id) {
        if (id == null) {
            return Result.error("id 不能为空");
        }
        List<Long> permissionIds = roleService.getPermissionIdsByRoleId(id);
        return Result.success("查询成功", permissionIds);
    }

    /**
    * @description: 根据角色id分配权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id, permissionIds]
    * @return: {@link Result}<{@link Void}>
    **/
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