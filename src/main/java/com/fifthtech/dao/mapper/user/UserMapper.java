package com.fifthtech.dao.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dto.user.UserQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author RH
 * @ClassName UserMapper
 * @description: 用户Mapper接口
 * @date 2026年01月25日
 * @version: 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
    * @description: 分页查询用户，根据组织集合过滤挂靠关系
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [page, query]
    * @return: {@link IPage}<{@link User}>
    **/
    IPage<User> listPage(Page<User> page, @Param("query") UserQueryDTO query);
}
