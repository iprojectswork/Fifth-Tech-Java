package com.fifthtech.dao.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author RH
 * @ClassName JsonbStringTypeHandler
 * @description: JSONB字符串类型处理器
 * @date 2026年08月16日
 * @version: 1.0
 */
@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonbStringTypeHandler extends BaseTypeHandler<String> {

    /**
    * @description: 将非空 String 写入为 PG JSONB（Types.OTHER）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ps, i, parameter, jdbcType]
    * @return: void
    **/
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setObject(i, parameter, Types.OTHER);
    }

    /**
    * @description: 按列名读取 JSONB，结果以 toString 形式返回（保留 JSON 原文）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [rs, columnName]
    * @return: {@link String}
    **/
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        return value == null ? null : value.toString();
    }

    /**
    * @description: 按列序号读取 JSONB，结果以 toString 形式返回
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [rs, columnIndex]
    * @return: {@link String}
    **/
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        return value == null ? null : value.toString();
    }

    /**
    * @description: 从 CallableStatement 按列序号读取 JSONB，结果以 toString 形式返回
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [cs, columnIndex]
    * @return: {@link String}
    **/
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex);
        return value == null ? null : value.toString();
    }
}