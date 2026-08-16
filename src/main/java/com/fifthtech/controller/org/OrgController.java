package com.fifthtech.controller.org;

import com.fifthtech.common.Result;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dto.org.OrgDTO;
import com.fifthtech.dto.org.OrgMembersDTO;
import com.fifthtech.dto.org.OrgMoveDTO;
import com.fifthtech.dto.org.OrgQueryDTO;
import com.fifthtech.service.org.OrgService;
import com.fifthtech.vo.org.OrgMemberVO;
import com.fifthtech.vo.org.OrgTreeVO;
import com.fifthtech.vo.org.OrgVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author RH
 * @ClassName OrgController
 * @description: 组织控制器
 * @date 2026年08月09日
 * @version: 1.0
 */
@RestController
@RequestMapping("/org")
public class OrgController {

    @Resource
    private OrgService orgService;

    /**
    * @description: 根据父id查询子组织
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link List}<{@link OrgVO}>>
    **/
    @GetMapping("/children")
    public Result<List<OrgVO>> listChildren(OrgQueryDTO query) {
        if (query == null) {
            query = new OrgQueryDTO();
        }
        if (query.getParentId() == null) {
            query.setParentId(0L);
        }
        return Result.success("查询成功", orgService.listChildren(query));
    }

    /**
    * @description: 根据父id查询组织列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link List}<{@link OrgVO}>>
    **/
    @GetMapping("/list")
    public Result<List<OrgVO>> list(OrgQueryDTO query) {
        if (query == null) {
            query = new OrgQueryDTO();
        }
        if (query.getParentId() == null) {
            query.setParentId(0L);
        }
        return Result.success("查询成功", orgService.list(query));
    }

    /**
    * @description: 查询组织树
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link Result}<{@link List}<{@link OrgTreeVO}>>
    **/
    @GetMapping("/tree")
    public Result<List<OrgTreeVO>> tree() {
        return Result.success("查询成功", orgService.tree());
    }

    /**
    * @description: 根据id查询组织
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Result}<{@link OrgVO}>
    **/
    @GetMapping("/info-by-id/{id}")
    public Result<OrgVO> info(@PathVariable Long id) {
        OrgVO vo = orgService.info(id);
        if (vo == null) {
            return Result.error("组织不存在");
        }
        return Result.success("查询成功", vo);
    }

    /**
    * @description: 新增组织
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link OrgVO}>
    **/
    @PostMapping
    public Result<OrgVO> insert(@RequestBody OrgDTO dto) {
        if (dto == null) {
            return Result.error("请求体不能为空");
        }
        try {
            return Result.success("添加成功", orgService.insert(dto));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 根据id修改组织
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link OrgVO}>
    **/
    @PutMapping
    public Result<OrgVO> edit(@RequestBody OrgDTO dto) {
        if (dto == null || dto.getId() == null) {
            return Result.error("id 不能为空");
        }
        try {
            return Result.success("更新成功", orgService.edit(dto));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 移动组织
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link Void}>
    **/
    @PutMapping("/move")
    public Result<Void> move(@RequestBody OrgMoveDTO dto) {
        if (dto == null) {
            return Result.error("请求体不能为空");
        }
        try {
            orgService.move(dto);
            return Result.success("移动成功", null);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 根据id删除组织
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
        try {
            orgService.delete(id);
            return Result.success("删除成功", null);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 查询组织选项
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link Result}<{@link List}<{@link OrgVO}>>
    **/
    @GetMapping("/options")
    public Result<List<OrgVO>> options() {
        return Result.success("查询成功", orgService.options());
    }

    /**
    * @description: 根据组织和角色查询用户
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link List}<{@link User}>>
    **/
    @GetMapping("/users-by-role")
    public Result<List<User>> usersByRole(OrgQueryDTO query) {
        try {
            return Result.success("查询成功", orgService.usersByRole(query));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 根据组织id查询成员
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link List}<{@link OrgMemberVO}>>
    **/
    @GetMapping("/members")
    public Result<List<OrgMemberVO>> listMembers(OrgQueryDTO query) {
        try {
            return Result.success("查询成功", orgService.listMembers(query));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 根据组织id保存成员
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link Void}>
    **/
    @PutMapping("/members")
    public Result<Void> replaceMembers(@RequestBody OrgMembersDTO dto) {
        if (dto == null || dto.getOrgId() == null) {
            return Result.error("orgId 不能为空");
        }
        try {
            orgService.replaceMembers(dto);
            return Result.success("保存成功", null);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }
}