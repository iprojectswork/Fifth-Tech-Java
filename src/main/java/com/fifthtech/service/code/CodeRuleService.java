package com.fifthtech.service.code;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.dao.entity.code.CodeRule;
import com.fifthtech.dao.entity.code.CodeSequence;
import com.fifthtech.dto.code.CodeRuleDTO;
import com.fifthtech.dto.code.CodeRuleQueryDTO;
import com.fifthtech.dto.code.CodeSequenceQueryDTO;

import java.util.List;

/**
 * CodeRuleService
 *
 * <p>负责规则 CRUD、片段校验、变更时清理对应 Redis 号段池。
 * 取号 / 试拼由 {@link CodeGenerateService} 承担。</p>
 *
 * @author RH
 * @description 编码规则服务接口
 * @date 2026-08-02
 */
public interface CodeRuleService extends IService<CodeRule> {

    /**
     * 规则分页列表（手写 SQL；按 ruleCode / ruleName / status 过滤）
     */
    Page<CodeRule> list(CodeRuleQueryDTO query);

    /**
     * 按主键取一条
     */
    CodeRule info(Long id);

    /**
     * 按 rule_code 取一条
     */
    CodeRule infoByRuleCode(String ruleCode);

    /**
     * 新增规则（含 segments 校验 + segments_json 序列化）
     */
    CodeRule insert(CodeRuleDTO dto);

    /**
     * 更新规则；status / segments / batch_size 变更时尝试清理对应 Redis pool key
     */
    CodeRule edit(CodeRuleDTO dto);

    /**
     * 逻辑删除；保留 sequence 历史
     */
    void delete(Long id);

    /**
     * 按规则取流水水位列表（{@code ruleId} 与 {@code ruleCode} 二选一；同时存在以 ruleId 为准）
     */
    List<CodeSequence> listSequences(CodeSequenceQueryDTO query);
}
