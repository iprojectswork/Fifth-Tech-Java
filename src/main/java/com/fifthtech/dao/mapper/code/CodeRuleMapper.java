package com.fifthtech.dao.mapper.code;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.code.CodeRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * CodeRuleMapper
 *
 * <p>{@code list} / {@code listByXxx} 走 {@code CodeRuleMapper.xml} 手写 SQL（D2）。</p>
 *
 * @author RH
 * @description 编码规则 Mapper
 * @date 2026-08-02
 */
@Mapper
public interface CodeRuleMapper extends BaseMapper<CodeRule> {

    /**
     * 编码规则分页列表（手写 SQL；显式列清单）
     *
     * @param ruleCode 规则编码（模糊，可空）
     * @param ruleName 规则名称（模糊，可空）
     * @param status   状态（可空）
     */
    List<CodeRule> selectPageList(@Param("ruleCode") String ruleCode,
                                  @Param("ruleName") String ruleName,
                                  @Param("status") Integer status,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);

    /**
     * 按 rule_code 查一条（生成服务加载规则用）
     */
    CodeRule selectByRuleCode(@Param("ruleCode") String ruleCode);
}
