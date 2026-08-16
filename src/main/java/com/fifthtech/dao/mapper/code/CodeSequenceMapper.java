package com.fifthtech.dao.mapper.code;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fifthtech.dao.entity.code.CodeSequence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author RH
 * @ClassName CodeSequenceMapper
 * @description: 编码流水水位Mapper接口
 * @date 2026年08月02日
 * @version: 1.0
 */
@Mapper
public interface CodeSequenceMapper extends BaseMapper<CodeSequence> {

    /**
    * @description: 根据规则id和周期键行锁查询水位
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ruleId, periodKey]
    * @return: {@link CodeSequence}
    **/
    CodeSequence selectForUpdate(@Param("ruleId") Long ruleId,
                                 @Param("periodKey") String periodKey);

    /**
    * @description: 首次写入水位行（currentMax = start - 1）；唯一键冲突时返回 0
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id, ruleId, ruleCode, periodKey, initialMax]
    * @return: int
    **/
    int insertInitial(@Param("id") Long id,
                      @Param("ruleId") Long ruleId,
                      @Param("ruleCode") String ruleCode,
                      @Param("periodKey") String periodKey,
                      @Param("initialMax") Long initialMax);

    /**
    * @description: 根据规则id和周期键自增水位
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ruleId, periodKey, increment]
    * @return: int
    **/
    int incrementCurrentMax(@Param("ruleId") Long ruleId,
                            @Param("periodKey") String periodKey,
                            @Param("increment") Long increment);
}
