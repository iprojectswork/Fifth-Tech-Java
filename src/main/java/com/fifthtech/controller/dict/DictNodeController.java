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
 * @author RH
 * @ClassName DictNodeController
 * @description: 数据字典节点控制器
 * @date 2026年08月02日
 * @version: 1.0
 */
@RestController
@RequestMapping("/dict")
public class DictNodeController {

    @Resource
    private DictNodeService dictNodeService;

    /**
    * @description: 根据父id查询子字典
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link List}<{@link DictNodeVO}>>
    **/
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
    * @description: 根据父id查询字典列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link List}<{@link DictNodeVO}>>
    **/
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
    * @description: 查询字典树
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link Result}<{@link List}<{@link DictNodeTreeVO}>>
    **/
    @GetMapping("/node/tree")
    public Result<List<DictNodeTreeVO>> tree() {
        return Result.success("查询成功", dictNodeService.tree());
    }

    /**
    * @description: 根据id查询字典节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Result}<{@link DictNodeVO}>
    **/
    @GetMapping("/node/info-by-id/{id}")
    public Result<DictNodeVO> info(@PathVariable Long id) {
        DictNodeVO vo = dictNodeService.info(id);
        if (vo == null) {
            return Result.error("节点不存在");
        }
        return Result.success("查询成功", vo);
    }

    /**
    * @description: 新增字典节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link DictNodeVO}>
    **/
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
    * @description: 根据id修改字典节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link DictNodeVO}>
    **/
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
    * @description: 移动字典
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link Void}>
    **/
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
    * @description: 根据id删除字典节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Result}<{@link Void}>
    **/
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
    * @description: 根据pathCode查询字典数据
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link List}<{@link DictNodeVO}>>
    **/
    @GetMapping("/data")
    public Result<List<DictNodeVO>> data(DictNodeQueryDTO query) {
        try {
            return Result.success("查询成功", dictNodeService.listDataByPathCode(query));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }
}