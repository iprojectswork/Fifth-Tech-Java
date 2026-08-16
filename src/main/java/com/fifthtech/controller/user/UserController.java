package com.fifthtech.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.common.Result;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dto.user.UserDTO;
import com.fifthtech.dto.user.UserQueryDTO;
import com.fifthtech.service.user.UserService;
import com.fifthtech.vo.permission.PermissionVO;
import com.fifthtech.vo.role.RoleVO;
import com.fifthtech.vo.user.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author RH
 * @ClassName UserController
 * @description: 用户控制器
 * @date 2026年01月25日
 * @version: 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
    * @description: 新增用户
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link UserVO}>
    **/
    @PostMapping
    public Result<UserVO> insert(@RequestBody UserDTO dto) {
        if (dto == null) {
            return Result.error("请求体不能为空");
        }
        try {
            User saved = userService.insertWithMemberships(dto);
            UserVO vo = userService.infoWithMemberships(saved.getId());
            return Result.success("添加成功", vo);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 根据id删除用户
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
        boolean success = userService.deleteUser(id);
        if (success) {
            return Result.success("删除成功", null);
        }
        return Result.error("删除失败，用户不存在");
    }

    /**
    * @description: 根据id修改用户
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link UserVO}>
    **/
    @PutMapping
    public Result<UserVO> edit(@RequestBody UserDTO dto) {
        if (dto == null || dto.getId() == null) {
            return Result.error("id 不能为空");
        }
        try {
            User saved = userService.editWithMemberships(dto);
            UserVO vo = userService.infoWithMemberships(saved.getId());
            return Result.success("更新成功", vo);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 根据id批量修改用户状态
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ids, status]
    * @return: {@link Result}<{@link Void}>
    **/
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody List<Long> ids, @RequestParam Integer status) {
        boolean success = userService.updateStatus(ids, status);
        if (success) {
            return Result.success("状态更新成功", null);
        }
        return Result.error("状态更新失败");
    }

    /**
    * @description: 根据id查询用户
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Result}<{@link UserVO}>
    **/
    @GetMapping("/{id}")
    public Result<UserVO> selectById(@PathVariable Long id) {
        if (id == null) {
            return Result.error("id 不能为空");
        }
        UserVO vo = userService.infoWithMemberships(id);
        if (vo != null) {
            return Result.success("查询成功", vo);
        }
        return Result.error("用户不存在！");
    }

    /**
    * @description: 根据用户id查询角色
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id, query]
    * @return: {@link Result}<{@link Page}<{@link RoleVO}>>
    **/
    @GetMapping("/{id}/roles")
    public Result<Page<RoleVO>> listRoles(@PathVariable Long id, UserQueryDTO query) {
        if (userService.selectById(id) == null) {
            return Result.error("用户不存在！");
        }
        if (query == null) {
            query = new UserQueryDTO();
        }
        query.setUserId(id);
        Page<Role> entityPage = userService.listRoles(query);
        Page<RoleVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(ConvertUtils.toVOList(entityPage.getRecords(), RoleVO.class));
        return Result.success("查询成功", voPage);
    }

    /**
    * @description: 根据用户id查询权限
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id, query]
    * @return: {@link Result}<{@link Page}<{@link PermissionVO}>>
    **/
    @GetMapping("/{id}/permissions")
    public Result<Page<PermissionVO>> listPermissions(@PathVariable Long id, UserQueryDTO query) {
        if (userService.selectById(id) == null) {
            return Result.error("用户不存在！");
        }
        if (query == null) {
            query = new UserQueryDTO();
        }
        query.setUserId(id);
        Page<Permission> entityPage = userService.listPermissions(query);
        Page<PermissionVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(ConvertUtils.toVOList(entityPage.getRecords(), PermissionVO.class));
        return Result.success("查询成功", voPage);
    }

    /**
    * @description: 分页查询用户列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link Page}<{@link UserVO}>>
    **/
    @GetMapping("/list")
    public Result<Page<UserVO>> list(UserQueryDTO query) {
        Page<User> entityPage = userService.list(query);
        Page<UserVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<UserVO> records = new java.util.ArrayList<>(entityPage.getRecords().size());
        for (User user : entityPage.getRecords()) {
            records.add(toListVO(user));
        }
        voPage.setRecords(records);
        return Result.success("查询成功", voPage);
    }

    /**
    * @description: 转换用户列表视图
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [user]
    * @return: {@link UserVO}
    **/
    private UserVO toListVO(User user) {
        return userService.infoWithMemberships(user.getId());
    }
}