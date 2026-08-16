package com.fifthtech.dao.mapper.code;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.code.CodeRule;
import com.fifthtech.dto.code.CodeRuleQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author RH
 * @ClassName CodeRuleMapper
 * @description: 编码规则Mapper接口
 * @date 2026年08月02日
 * @version: 1.0
 */
@Mapper
public interface CodeRuleMapper extends BaseMapper<CodeRule> {

    /**
    * @description: 分页查询规则，按 ruleCode/ruleName 模糊与 status 精确过滤（手写 SQL，显式列）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link List}<{@link CodeRule}>
    **/
    List<CodeRule> selectPageList(@Param("query") CodeRuleQueryDTO query);

    /**
    * @description: 根据规则编码查询未删规则
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ruleCode]
    * @return: {@link CodeRule}
    **/
    CodeRule selectByRuleCode(@Param("ruleCode") String ruleCode);
}
