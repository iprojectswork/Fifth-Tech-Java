package com.fifthtech.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @author RH
 * @ClassName UserOrgSummaryVO
 * @description: 用户成员组织摘要
 * @date 2026年08月09日
 * @version: 1.0
 */
@Data
public class UserOrgSummaryVO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 组织 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 组织编码
     */
    private String orgCode;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织名称路径
     */
    private String pathName;
}