package com.fifthtech.service.permission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.controller.permission.PermissionTreeVO;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.controller.permission.PermissionDTO;

import java.util.List;

public interface PermissionService extends IService<Permission> {

    Page<Permission> selectPage(Integer current, Integer size, String permissionName, String permissionCode);

    Permission selectById(Long id);

    List<Permission> selectAll();

    List<PermissionTreeVO> selectTree();

    Permission insert(PermissionDTO dto);

    Permission update(PermissionDTO dto);

    void deleteById(Long id);

    List<Permission> getPermissionsByUserId(Long userId);

    List<Permission> getMenuPermissionsByUserId(Long userId);

    List<String> getPermissionCodesByUserId(Long userId);

    List<PermissionTreeVO> getMenuTreeByUserId(Long userId);
}
