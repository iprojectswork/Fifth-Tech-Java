package com.fifthtech.dao.entity.dict;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DictNode
 *
 * <p>数据字典节点（单表树）。{@code parent_id=0} 表示根下第一层；path 不落库，
 * 由接口运行时按链拼接。同 parent 下未删 code 唯一（部分唯一索引）。</p>
 *
 * @author RH
 * @description 数据字典节点实体
 * @date 2026-08-02
 */
@Data
@TableName("sys_dict_node")
public class DictNode {

    /**
     * 主键（雪花 ID）
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField("id")
    private Long id;

    /**
     * 父节点 ID（0 表示根下第一层）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 节点编码（同 parent 下唯一；不可含 /；trim 后非空）
     */
    @TableField("code")
    private String code;

    /**
     * 节点名称
     */
    @TableField("name")
    private String name;

    /**
     * 排序（同父内；默认 0；列表/树子节点顺序 sort ASC, code ASC, id ASC）
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 状态（1 启用 / 0 禁用；默认 1）
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
     * 逻辑删除（0 未删 / 1 已删）
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