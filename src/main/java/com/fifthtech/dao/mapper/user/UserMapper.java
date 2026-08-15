package com.fifthtech.dao.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dto.user.UserQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * UserMapper
 *
 * <p>用户 Mapper。{@code list} 走 XML 手写 SQL（{@code EXISTS sys_user_org} 过滤 +
 * 显式列；C4 §7.3 禁 {@code SELECT *}）。查询入参一律封装为 {@link UserQueryDTO}。</p>
 *
 * @author RH
 * @description 用户Mapper接口
 * @date 2026-01-25
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 用户分页。{@code query.orgIds} 可选；存在时挂靠落在该集合内即命中（含下级组织）。
     */
    IPage<User> listPage(Page<User> page, @Param("query") UserQueryDTO query);
}
