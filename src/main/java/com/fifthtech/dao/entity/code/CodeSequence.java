package com.fifthtech.dao.entity.code;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * CodeSequence
 *
 * <p>流水账本：每个 {@code (rule_id, period_key)} 一行，记录该周期已分配到的最大序号
 * {@code current_max}。运行期补段时由 {@code CodeGenerateService} 在行锁内递增。</p>
 *
 * @author RH
 * @description 编码流水水位实体
 * @date 2026-08-02
 */
@Data
@TableName("sys_code_sequence")
public class CodeSequence {

    /**
     * 主键（雪花 ID）
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
     * 关联 sys_code_rule.id
     */
    @TableField("rule_id")
    private Long ruleId;

    /**
     * 周期键（GLOBAL / Y:yyyy / M:yyyyMM / D:yyyyMMdd）
     */
    @TableField("period_key")
    private String periodKey;

    /**
     * 已预支到的最大序号（含）；下次预支从 current_max+1 起
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
