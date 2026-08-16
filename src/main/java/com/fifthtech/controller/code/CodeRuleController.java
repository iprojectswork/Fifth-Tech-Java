package com.fifthtech.controller.code;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.common.Result;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.code.CodeRule;
import com.fifthtech.dto.code.CodeRuleDTO;
import com.fifthtech.dto.code.CodeRuleQueryDTO;
import com.fifthtech.service.code.CodeRuleService;
import com.fifthtech.vo.code.CodeRuleVO;
import com.fifthtech.vo.code.CodeSegmentVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * @author RH
 * @ClassName CodeRuleController
 * @description: 编码规则控制器
 * @date 2026年08月02日
 * @version: 1.0
 */
@RestController
@RequestMapping("/code/rule")
public class CodeRuleController {

    @Resource
    private CodeRuleService codeRuleService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
    * @description: 分页查询编码规则列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link Page}<{@link CodeRuleVO}>>
    **/
    @GetMapping("/list")
    public Result<Page<CodeRuleVO>> list(CodeRuleQueryDTO query) {
        Page<CodeRule> entityPage = codeRuleService.list(query);
        Page<CodeRuleVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(toVOList(entityPage.getRecords()));
        return Result.success("查询成功", voPage);
    }

    /**
    * @description: 根据id查询编码规则
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link Result}<{@link CodeRuleVO}>
    **/
    @GetMapping("/{id}")
    public Result<CodeRuleVO> info(@PathVariable Long id) {
        CodeRule rule = codeRuleService.info(id);
        if (rule == null) {
            return Result.error("规则不存在");
        }
        return Result.success("查询成功", toVO(rule));
    }

    /**
    * @description: 新增编码规则
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link CodeRuleVO}>
    **/
    @PostMapping
    public Result<CodeRuleVO> insert(@RequestBody CodeRuleDTO dto) {
        if (dto == null) {
            return Result.error("请求体不能为空");
        }
        try {
            CodeRule rule = codeRuleService.insert(dto);
            return Result.success("添加成功", toVO(rule));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 根据id修改编码规则
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link CodeRuleVO}>
    **/
    @PutMapping
    public Result<CodeRuleVO> edit(@RequestBody CodeRuleDTO dto) {
        if (dto == null || dto.getId() == null) {
            return Result.error("id 不能为空");
        }
        try {
            CodeRule rule = codeRuleService.edit(dto);
            return Result.success("更新成功", toVO(rule));
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 根据id删除编码规则
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
        codeRuleService.delete(id);
        return Result.success("删除成功", null);
    }

    /**
    * @description: 转换编码规则视图
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [rule]
    * @return: {@link CodeRuleVO}
    **/
    private CodeRuleVO toVO(CodeRule rule) {
        if (rule == null) {
            return null;
        }
        CodeRuleVO vo = ConvertUtils.toVO(rule, CodeRuleVO.class);
        vo.setSegments(parseSegments(rule.getSegmentsJson()));
        return vo;
    }

    /**
    * @description: 转换编码规则列表视图
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [list]
    * @return: {@link List}<{@link CodeRuleVO}>
    **/
    private List<CodeRuleVO> toVOList(List<CodeRule> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<CodeRuleVO> out = new java.util.ArrayList<>(list.size());
        for (CodeRule rule : list) {
            out.add(toVO(rule));
        }
        return out;
    }

    /**
    * @description: 解析规则片段
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [json]
    * @return: {@link List}<{@link CodeSegmentVO}>
    **/
    private List<CodeSegmentVO> parseSegments(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<CodeSegmentVO>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}