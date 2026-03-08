package com.fifthtech.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.common.Result;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * UserController
 *
 * @author RH
 * @description 用户控制�? * @date 2026-01-25
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public Result<UserVO> insert(@RequestBody UserDTO dto) {
        User entity = ConvertUtils.toEntity(dto, User.class);
        User savedEntity = userService.insert(entity);
        UserVO vo = ConvertUtils.toVO(savedEntity, UserVO.class);
        return Result.success("添加成功", vo);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = userService.delete(id);
        if (success) {
            return Result.success("删除成功", null);
        } else {
            return Result.error("删除失败，用户不存在");
        }
    }

    @PutMapping
    public Result<UserVO> edit(@RequestBody UserDTO dto) {
        User entity = ConvertUtils.toEntity(dto, User.class);
        boolean success = userService.edit(entity);
        if (success) {
            User updatedEntity = userService.selectById(entity.getId());
            UserVO vo = ConvertUtils.toVO(updatedEntity, UserVO.class);
            return Result.success("更新成功", vo);
        } else {
            return Result.error("更新失败，用户不存在");
        }
    }

    @GetMapping("/{id}")
    public Result<UserVO> selectById(@PathVariable Long id) {
        User entity = userService.selectById(id);
        if (entity != null) {
            UserVO vo = ConvertUtils.toVO(entity, UserVO.class);
            return Result.success("查询成功", vo);
        } else {
            return Result.error("用户不存在！");
        }
    }

    @GetMapping("/list")
    public Result<Page<UserVO>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            UserDTO query) {
        User queryEntity = ConvertUtils.toEntity(query, User.class);
        Page<User> entityPage = userService.list(current, size, queryEntity);
        Page<UserVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(ConvertUtils.toVOList(entityPage.getRecords(), UserVO.class));
        return Result.success("查询成功", voPage);
    }
}
