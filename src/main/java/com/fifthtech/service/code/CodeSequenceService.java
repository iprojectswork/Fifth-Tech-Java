package com.fifthtech.service.code;

import com.fifthtech.dao.entity.code.CodeRule;
import com.fifthtech.dto.code.CodeSegmentDTO;

import java.util.List;

/**
 * CodeSequenceService
 *
 * <p>专做号段预支（{@code REQUIRES_NEW} 事务，独立提交）。与
 * {@link CodeGenerateServiceImpl} 解耦，避免 Spring AOP 自调用问题。</p>
 *
 * @author RH
 * @description 流水水位内部服务（事务边界）
 * @date 2026-08-02
 */
public interface CodeSequenceService {

    /**
     * 在独立事务内为 (rule, periodKey) 预支一个批次。
     *
     * @return 批次内的连续序号（升序）
     */
    List<Long> allocateBatch(CodeRule rule, CodeSegmentDTO sequenceSegment, String periodKey);

    /**
     * 只读查询水位 current_max；不存在返回 null。
     */
    Long findCurrentMax(Long ruleId, String periodKey);
}
