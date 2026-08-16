package com.fifthtech.dao.entity.code;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fifthtech.dao.handler.JsonbStringTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author RH
 * @ClassName CodeRule
 * @description: 编码规则实体
 * @date 2026年08月02日
 * @version: 1.0
 */
@Data
@TableName(value = "sys_code_rule", autoResultMap = true)
public class CodeRule {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("id")
    private Long id;

    /**
     * 规则编码（全局唯一）
     */
    @TableField("rule_code")
    private String ruleCode;

    /**
     * 规则名称
     */
    @TableField("rule_name")
    private String ruleName;

    /**
     * 有序片段数组的 JSON 字符串
     */
    @TableField(value = "segments_json", typeHandler = JsonbStringTypeHandler.class)
    private String segmentsJson;

    /**
     * 预支号段大小（默认 100）
     */
    @TableField("batch_size")
    private Integer batchSize;

    /**
     * 状态（1：启用 0：禁用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建人 ID
     */
    @TableField("create_id")
    private Long createId;

    /**
     * 创建人姓名
     */
    @TableField("create_name")
    private String createName;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新人 ID
     */
    @TableField("update_id")
    private Long updateId;

    /**
     * 更新人姓名
     */
    @TableField("update_name")
    private String updateName;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除（0：否 1：是）
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 删除人 ID
     */
    @TableField("delete_id")
    private Long deleteId;

    /**
     * 删除人姓名
     */
    @TableField("delete_name")
    private String deleteName;

    /**
     * 删除时间
     */
    @TableField("delete_time")
    private LocalDateTime deleteTime;
}