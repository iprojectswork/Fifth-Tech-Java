package com.fifthtech.dao.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.dao.entity.permission.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    List<Permission> selectByUserId(@Param("userId") Long userId);

    IPage<Permission> listRelatedByUserId(Page<Permission> page, @Param("userId") Long userId);

    List<Permission> selectMenuByUserId(@Param("userId") Long userId);

    List<Permission> selectAllPermissions();

    List<Permission> selectByParentId(@Param("parentId") Long parentId);

    List<Long> selectParentIdsHavingChildren(@Param("parentIds") List<Long> parentIds);
}
