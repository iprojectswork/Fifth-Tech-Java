package com.fifthtech.dao.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.permission.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    List<Permission> selectByUserId(@Param("userId") Long userId);

    List<Permission> selectMenuByUserId(@Param("userId") Long userId);

    List<Permission> selectAllPermissions();
}
