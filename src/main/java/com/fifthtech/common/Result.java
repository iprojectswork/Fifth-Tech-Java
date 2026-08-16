package com.fifthtech.common;

/**
 * @author RH
 * @ClassName Result
 * @description: 统一响应
 * @date 2026年01月25日
 * @version: 1.0
 */
public class Result<T> {

    /**
     * 响应码（200 成功；401 未登录；500 业务失败）
     */
    private Integer code;

    /**
     * 响应描述
     */
    private String message;

    /**
     * 业务数据
     */
    private T data;

    /**
    * @description: 无参构造器
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: 
    **/
    public Result() {
    }

    /**
    * @description: 全参构造器
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [code, message, data]
    * @return: 
    **/
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
    * @description: 成功响应（默认消息 "操作成功"）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [data]
    * @return: {@link Result}<{@link T}>
    **/
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
    * @description: 成功响应（自定义消息）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [message, data]
    * @return: {@link Result}<{@link T}>
    **/
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
    * @description: 业务失败响应（默认 code=500）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [message]
    * @return: {@link Result}<{@link T}>
    **/
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
    * @description: 业务失败响应（自定义响应码）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [code, message]
    * @return: {@link Result}<{@link T}>
    **/
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}