package com.fifthtech.dao.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.dao.entity.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UserMapper
 *
 * <p>用户 Mapper。{@code list} 走 XML 手写 SQL（{@code EXISTS sys_user_org} 过滤 +
 * 显式列；C4 §7.3 禁 {@code SELECT *}）。</p>
 *
 * @author RH
 * @description 用户Mapper接口
 * @date 2026-01-25
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 用户分页。{@code orgIds} 可选；存在时挂靠落在该集合内即命中（含下级组织）。
     */
    IPage<User> listPage(Page<User> page,
                         @Param("username") String username,
                         @Param("nickname") String nickname,
                         @Param("email") String email,
                         @Param("phone") String phone,
                         @Param("status") Integer status,
                         @Param("orgIds") List<Long> orgIds);
}