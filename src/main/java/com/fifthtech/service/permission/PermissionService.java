package com.fifthtech.service.permission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.vo.permission.PermissionTreeVO;
import com.fifthtech.vo.permission.PermissionVO;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.dto.permission.PermissionDTO;
import com.fifthtech.dto.permission.PermissionQueryDTO;

import java.util.List;

public interface PermissionService extends IService<Permission> {

    Page<Permission> selectPage(PermissionQueryDTO query);

    Permission selectById(Long id);

    List<Permission> selectAll();

    List<PermissionTreeVO> selectTree();

    List<PermissionVO> listChildren(PermissionQueryDTO query);

    Permission insert(PermissionDTO dto);

    Permission update(PermissionDTO dto);

    void deleteById(Long id);

    List<Permission> getPermissionsByUserId(Long userId);

    List<Permission> getMenuPermissionsByUserId(Long userId);

    List<String> getPermissionCodesByUserId(Long userId);

    List<PermissionTreeVO> getMenuTreeByUserId(Long userId);
}
