package com.fifthtech.dao.mapper.code;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.code.CodeSequence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * CodeSequenceMapper
 *
 * <p>号段池分配用事务 SQL（{@code SELECT ... FOR UPDATE} + {@code UPDATE current_max}）。</p>
 *
 * @author RH
 * @description 编码流水水位 Mapper
 * @date 2026-08-02
 */
@Mapper
public interface CodeSequenceMapper extends BaseMapper<CodeSequence> {

    /**
     * 行锁选择；找不到返回 null（由调用方决定 insert 或重新 select）
     */
    CodeSequence selectForUpdate(@Param("ruleId") Long ruleId,
                                 @Param("periodKey") String periodKey);

    /**
     * 首次分配（start-1 落入 current_max）；唯一键冲突时由调用方处理
     *
     * @return 受影响行数（0 = 已存在被并发抢占，1 = 插入成功）
     */
    int insertInitial(@Param("id") Long id,
                      @Param("ruleId") Long ruleId,
                      @Param("ruleCode") String ruleCode,
                      @Param("periodKey") String periodKey,
                      @Param("initialMax") Long initialMax);

    /**
     * 预支号段：在行锁事务内将 current_max 增加 {@code increment}。
     *
     * @return 受影响行数
     */
    int incrementCurrentMax(@Param("ruleId") Long ruleId,
                            @Param("periodKey") String periodKey,
                            @Param("increment") Long increment);
}
