package com.fifthtech.dao.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.user.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserMapper
 *
 * @author RH
 * @description 用户Mapper接口
 * @date 2026-01-25
 * @version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
