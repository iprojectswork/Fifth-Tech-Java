package com.fifthtech.security;

/**
 * @author RH
 * @ClassName UserContext
 * @description: 用户上下文
 * @date 2026年08月16日
 * @version: 1.0
 */
public class UserContext {

    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    /**
    * @description: 设置当前线程的用户 ID（拦截器校验通过后写入）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [userId]
    * @return: void
    **/
    public static void setCurrentUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    /**
    * @description: 取当前线程的用户 ID
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link Long}
    **/
    public static Long getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    /**
    * @description: 清理当前线程的用户 ID（请求结束时调用，避免线程复用泄漏）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: void
    **/
    public static void clear() {
        CURRENT_USER_ID.remove();
    }
}