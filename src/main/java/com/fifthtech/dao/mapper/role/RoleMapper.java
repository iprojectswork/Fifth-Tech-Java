package com.fifthtech.dao.mapper.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.role.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<Role> selectByUserId(@Param("userId") Long userId);

    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
