package com.fifthtech.dao.entity.code;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author RH
 * @ClassName CodeSequence
 * @description: 编码流水水位实体
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
@TableName("sys_code_sequence")
public class CodeSequence {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("id")
    private Long id;

    /**
     * 规则编码（冗余便于查询）
     */
    @TableField("rule_code")
    private String ruleCode;

    /**
     * 规则 ID
     */
    @TableField("rule_id")
    private Long ruleId;

    /**
     * 周期键（GLOBAL / Y:yyyy / M:yyyyMM / D:yyyyMMdd）
     */
    @TableField("period_key")
    private String periodKey;

    /**
     * 已预支到的最大序号（含）
     */
    @TableField("current_max")
    private Long currentMax;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}