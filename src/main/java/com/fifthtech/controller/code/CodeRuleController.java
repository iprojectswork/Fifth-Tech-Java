package com.fifthtech.controller.code;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fifthtech.common.Result;
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
 * CodeRuleController
 *
 * <p>编码规则 CRUD；取号 / 试拼由 {@link CodeGenerateController} 提供，无业务权限码。</p>
 *
 * @author RH
 * @description 编码规则控制器
 * @date 2026-08-02
 */
@RestController
@RequestMapping("/code/rule")
public class CodeRuleController {

    @Resource
    private CodeRuleService codeRuleService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分页列表（手写 SQL）
     */
    @GetMapping("/list")
    public Result<Page<CodeRuleVO>> list(CodeRuleQueryDTO query) {
        Page<CodeRule> entityPage = codeRuleService.list(query);
        Page<CodeRuleVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(toVOList(entityPage.getRecords()));
        return Result.success("查询成功", voPage);
    }

    /**
     * 详情（D3：info 用 MP）
     */
    @GetMapping("/{id}")
    public Result<CodeRuleVO> info(@PathVariable Long id) {
        CodeRule rule = codeRuleService.info(id);
        if (rule == null) {
            return Result.error("规则不存在");
        }
        return Result.success("查询成功", toVO(rule));
    }

    /**
     * 新增
     */
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
     * 更新
     */
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
     * 逻辑删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (id == null) {
            return Result.error("id 不能为空");
        }
        codeRuleService.delete(id);
        return Result.success("删除成功", null);
    }

    private CodeRuleVO toVO(CodeRule rule) {
        if (rule == null) {
            return null;
        }
        CodeRuleVO vo = new CodeRuleVO();
        vo.setId(rule.getId());
        vo.setRuleCode(rule.getRuleCode());
        vo.setRuleName(rule.getRuleName());
        vo.setBatchSize(rule.getBatchSize());
        vo.setStatus(rule.getStatus());
        vo.setRemark(rule.getRemark());
        vo.setCreateId(rule.getCreateId());
        vo.setCreateName(rule.getCreateName());
        vo.setCreateTime(rule.getCreateTime());
        vo.setUpdateId(rule.getUpdateId());
        vo.setUpdateName(rule.getUpdateName());
        vo.setUpdateTime(rule.getUpdateTime());
        vo.setSegments(parseSegments(rule.getSegmentsJson()));
        return vo;
    }

    private List<CodeRuleVO> toVOList(List<CodeRule> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<CodeRuleVO> out = new java.util.ArrayList<>(list.size());
        for (CodeRule r : list) {
            out.add(toVO(r));
        }
        return out;
    }

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
