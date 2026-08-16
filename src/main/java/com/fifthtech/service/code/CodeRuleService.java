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
 * @author RH
 * @ClassName CodeRuleService
 * @description: 编码规则服务接口
 * @date 2026年08月02日
 * @version: 1.0
 */
public interface CodeRuleService extends IService<CodeRule> {

    /**
    * @description: 分页查询规则，按 ruleCode/ruleName 模糊与 status 精确过滤
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Page}<{@link CodeRule}>
    **/
    Page<CodeRule> list(CodeRuleQueryDTO query);

    /**
    * @description: 根据id查询规则
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link CodeRule}
    **/
    CodeRule info(Long id);

    /**
    * @description: 根据规则编码查询规则
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ruleCode]
    * @return: {@link CodeRule}
    **/
    CodeRule infoByRuleCode(String ruleCode);

    /**
    * @description: 新增规则，校验 segments 合法性并序列化为 segments_json 落库
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link CodeRule}
    **/
    CodeRule insert(CodeRuleDTO dto);

    /**
    * @description: 根据id修改规则，变更片段或批次后清理号段池
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link CodeRule}
    **/
    CodeRule edit(CodeRuleDTO dto);

    /**
    * @description: 根据id逻辑删除规则，保留流水历史水位
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: void
    **/
    void delete(Long id);

    /**
    * @description: 根据规则id或规则编码查询流水水位
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link List}<{@link CodeSequence}>
    **/
    List<CodeSequence> listSequences(CodeSequenceQueryDTO query);
}
