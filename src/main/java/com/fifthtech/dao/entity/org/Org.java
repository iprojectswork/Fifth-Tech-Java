package com.fifthtech.dao.entity.org;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author RH
 * @ClassName Org
 * @description: 组织单元实体
 * @date 2026年08月09日
 * @version: 1.0
 */
@Data
@TableName("sys_org")
public class Org {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("id")
    private Long id;

    /**
     * 父组织 ID（0 表示根下第一层）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 组织编码（同 parent 下唯一）
     */
    @TableField("org_code")
    private String orgCode;

    /**
     * 组织名称
     */
    @TableField("org_name")
    private String orgName;

    /**
     * 组织类型（数据字典 org/type 的 code）
     */
    @TableField("org_type")
    private String orgType;

    /**
     * 排序（同父内）
     */
    @TableField("sort")
    private Integer sort;

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