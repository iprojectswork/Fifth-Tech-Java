package com.fifthtech.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.common.Result;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.permission.Permission;
import com.fifthtech.dao.entity.role.Role;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dto.user.UserDTO;
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
 * UserController
 *
 * <p>用户 API。
 * <ul>
 *   <li>{@code POST /user} / {@code PUT /user}：可选 {@code roleIds[]}；不写组织成员。</li>
 *   <li>{@code GET /user/list}：可选 {@code orgId} 精确过滤该组织成员。</li>
 *   <li>{@code GET /user/{id}}：详情含 {@code orgs[]}（只读）/ {@code roles[]}。</li>
 * </ul>
 * </p>
 *
 * @author RH
 * @description 用户控制器
 * @date 2026-01-25
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

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

    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody List<Long> ids, @RequestParam Integer status) {
        boolean success = userService.updateStatus(ids, status);
        if (success) {
            return Result.success("状态更新成功", null);
        }
        return Result.error("状态更新失败");
    }

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

    @GetMapping("/{id}/roles")
    public Result<Page<RoleVO>> listRoles(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        if (userService.selectById(id) == null) {
            return Result.error("用户不存在！");
        }
        Page<Role> entityPage = userService.listRoles(id, current, size);
        Page<RoleVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(ConvertUtils.toVOList(entityPage.getRecords(), RoleVO.class));
        return Result.success("查询成功", voPage);
    }

    @GetMapping("/{id}/permissions")
    public Result<Page<PermissionVO>> listPermissions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        if (userService.selectById(id) == null) {
            return Result.error("用户不存在！");
        }
        Page<Permission> entityPage = userService.listPermissions(id, current, size);
        Page<PermissionVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(ConvertUtils.toVOList(entityPage.getRecords(), PermissionVO.class));
        return Result.success("查询成功", voPage);
    }

    /**
     * 分页查询。可选 {@code orgId} 精确过滤该组织成员。
     */
    @GetMapping("/list")
    public Result<Page<UserVO>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            UserDTO query) {
        Page<User> entityPage = userService.list(current, size, query);
        Page<UserVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<UserVO> records = new java.util.ArrayList<>(entityPage.getRecords().size());
        for (User u : entityPage.getRecords()) {
            records.add(toListVO(u));
        }
        voPage.setRecords(records);
        return Result.success("查询成功", voPage);
    }

    /**
     * 列表视图：基本字段 + orgNames 摘要（每行一次 infoWithMemberships 汇总；
     * 一期不做 N+1 优化；分页 ≤ 20 性能可接受）。
     */
    private UserVO toListVO(User u) {
        return userService.infoWithMemberships(u.getId());
    }
}