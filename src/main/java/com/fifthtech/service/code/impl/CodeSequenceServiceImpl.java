package com.fifthtech.service.code.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fifthtech.dao.entity.code.CodeRule;
import com.fifthtech.dao.entity.code.CodeSequence;
import com.fifthtech.dao.mapper.code.CodeSequenceMapper;
import com.fifthtech.dto.code.CodeSegmentDTO;
import com.fifthtech.service.code.CodeSequenceService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * CodeSequenceServiceImpl
 *
 * <p>所有 SQL 走独立事务（{@code REQUIRES_NEW}），供 {@link CodeGenerateServiceImpl} 通过
 * Spring 代理调用，保证行锁 + 自增在事务内完成。</p>
 *
 * @author RH
 * @description 流水水位事务服务实现
 * @date 2026-08-02
 */
@Service
public class CodeSequenceServiceImpl implements CodeSequenceService {

    @Resource
    private CodeSequenceMapper codeSequenceMapper;

    @Value("${code.generate.default-pool-batch-size:100}")
    private int defaultPoolBatchSize;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public List<Long> allocateBatch(CodeRule rule, CodeSegmentDTO sequenceSegment, String periodKey) {
        int batchSize = rule.getBatchSize() != null && rule.getBatchSize() > 0 ? rule.getBatchSize() : defaultPoolBatchSize;
        long start = sequenceSegment.getStart() == null ? 1L : sequenceSegment.getStart();
        long step = sequenceSegment.getStep() == null ? 1L : sequenceSegment.getStep();
        if (step <= 0) {
            throw new IllegalArgumentException("SEQUENCE.step 必须 > 0");
        }

        CodeSequence row = codeSequenceMapper.selectForUpdate(rule.getId(), periodKey);
        if (row == null) {
            codeSequenceMapper.insertInitial(
                    IdWorker.getId(), rule.getId(), rule.getRuleCode(), periodKey, start - 1);
            row = codeSequenceMapper.selectForUpdate(rule.getId(), periodKey);
            if (row == null) {
                throw new RuntimeException("初始化流水水位失败");
            }
        }
        long oldMax = row.getCurrentMax() == null ? (start - 1) : row.getCurrentMax();
        long increment = (long) batchSize * step;
        long newMax = oldMax + increment;
        int updated = codeSequenceMapper.incrementCurrentMax(rule.getId(), periodKey, increment);
        if (updated == 0) {
            throw new RuntimeException("预支号段失败");
        }
        List<Long> batch = new ArrayList<>(batchSize);
        for (long v = oldMax + step; v <= newMax; v += step) {
            batch.add(v);
        }
        return batch;
    }

    @Override
    public Long findCurrentMax(Long ruleId, String periodKey) {
        LambdaQueryWrapper<CodeSequence> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CodeSequence::getRuleId, ruleId)
                .eq(CodeSequence::getPeriodKey, periodKey);
        CodeSequence row = codeSequenceMapper.selectOne(wrapper);
        return row == null ? null : row.getCurrentMax();
    }
}
