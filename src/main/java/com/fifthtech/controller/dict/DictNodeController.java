package com.fifthtech.controller.dict;

import com.fifthtech.common.Result;
import com.fifthtech.dto.dict.DictNodeDTO;
import com.fifthtech.dto.dict.DictNodeMoveDTO;
import com.fifthtech.dto.dict.DictNodeQueryDTO;
import com.fifthtech.service.dict.DictNodeService;
import com.fifthtech.vo.dict.DictNodeTreeVO;
import com.fifthtech.vo.dict.DictNodeVO;
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
 * DictNodeController
 *
 * <p>数据字典节点 API（C3 §7.2）。仅 Token 登录拦截，不做 L3 权限码校验。
 * 写操作（insert/edit/delete/move）业务校验与事务均在 Service 层。</p>
 *
 * @author RH
 * @description 数据字典节点控制器
 * @date 2026-08-02
 */
@RestController
@RequestMapping("/dict")
public class DictNodeController {

    @Resource
    private DictNodeService dictNodeService;

    /**
     * 懒加载 children：左树展开节点时调用
     */
    @GetMapping("/node/children")
    public Result<List<DictNodeVO>> listChildren(DictNodeQueryDTO query) {
        if (query == null) {
            query = new DictNodeQueryDTO();
        }
        if (query.getParentId() == null) {
            query.setParentId(0L);
        }
        return Result.success("查询成功", dictNodeService.listChildren(query));
    }

    /**
     * 右栏 list（与 children 同数据源，方法别名）
     */
    @GetMapping("/node/list")
    public Result<List<DictNodeVO>> list(DictNodeQueryDTO query) {
        if (query == null) {
            query = new DictNodeQueryDTO();
        }
        if (query.getParentId() == null) {
            query.setParentId(0L);
        }
        return Result.success("查询成功", dictNodeService.list(query));
    }

    /**
     * 全量树（含叶子与 path）
     */
    @GetMapping("/node/tree")
    public Result<List<DictNodeTreeVO>> tree() {
        return Result.success("查询成功", dictNodeService.tree());
    }

    /**
     * 详情
     */
    @GetMapping("/node/info-by-id/{id}")
    public Result<DictNodeVO> info(@PathVariable Long id) {
        DictNodeVO vo = dictNodeService.info(id);
        if (vo == null) {
            return Result.error("节点不存在");
        }
        return Result.success("查询成功", vo);
    }

    /**
     * 新增节点
     */
    @PostMapping("/node")
    public Result<DictNodeVO> insert(@RequestBody DictNodeDTO dto) {
        if (dto == null) {
            return Result.error("请求体不能为空");
        }
        try {
            return Result.success("添加成功", dictNodeService.insert(dto));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 修改节点（不允许通过本接口改 parent；改挂请走 /dict/node/move）
     */
    @PutMapping("/node")
    public Result<DictNodeVO> edit(@RequestBody DictNodeDTO dto) {
        if (dto == null || dto.getId() == null) {
            return Result.error("id 不能为空");
        }
        try {
            return Result.success("更新成功", dictNodeService.edit(dto));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 改挂父节点（仅无未删子节点可拖；服务端权威校验）
     */
    @PutMapping("/node/move")
    public Result<Void> move(@RequestBody DictNodeMoveDTO dto) {
        if (dto == null) {
            return Result.error("请求体不能为空");
        }
        try {
            dictNodeService.move(dto);
            return Result.success("移动成功", null);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 逻辑删除（有未删子则后端拒绝）
     */
    @DeleteMapping("/node/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (id == null) {
            return Result.error("id 不能为空");
        }
        try {
            dictNodeService.delete(id);
            return Result.success("删除成功", null);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 业务只读：按 pathCode 取该节点下启用直接子（不存在 → 业务错误）
     */
    @GetMapping("/data")
    public Result<List<DictNodeVO>> data(DictNodeQueryDTO query) {
        try {
            return Result.success("查询成功", dictNodeService.listDataByPathCode(query));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }
}