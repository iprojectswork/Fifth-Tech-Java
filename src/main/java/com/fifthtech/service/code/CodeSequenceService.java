package com.fifthtech.service.code;

import com.fifthtech.dao.entity.code.CodeRule;
import com.fifthtech.dto.code.CodeSegmentDTO;

import java.util.List;

/**
 * @author RH
 * @ClassName CodeSequenceService
 * @description: 流水水位内部服务（事务边界）接口
 * @date 2026年08月02日
 * @version: 1.0
 */
public interface CodeSequenceService {

    /**
    * @description: 在独立事务（REQUIRES_NEW）内为 (rule, periodKey) 预支一个号段批次
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [rule, sequenceSegment, periodKey]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> allocateBatch(CodeRule rule, CodeSegmentDTO sequenceSegment, String periodKey);

    /**
    * @description: 只读查询 (ruleId, periodKey) 对应的水位 currentMax，不存在返回 null
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ruleId, periodKey]
    * @return: {@link Long}
    **/
    Long findCurrentMax(Long ruleId, String periodKey);
}
