package com.fifthtech.common;

/**
 * @author RH
 * @ClassName BizConstants
 * @description: 业务常量
 * @date 2026年08月15日
 * @version: 1.0
 */
public final class BizConstants {

    /**
    * @description: 禁止实例化
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: void
    **/
    private BizConstants() {
    }

    /**
     * 禁用 / 停用
     */
    public static final int STATUS_DISABLED = 0;

    /**
     * 启用
     */
    public static final int STATUS_ENABLED = 1;

    /**
     * 未逻辑删除
     */
    public static final int NOT_DELETED = 0;

    /**
     * 已逻辑删除
     */
    public static final int DELETED = 1;

    /**
     * 树根的 parent_id
     */
    public static final long ROOT_PARENT_ID = 0L;

    /**
     * 权限类型：菜单
     */
    public static final int PERMISSION_TYPE_MENU = 1;

    /**
     * 权限类型：按钮
     */
    public static final int PERMISSION_TYPE_BUTTON = 2;

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_CURRENT = 1;

    /**
     * 默认每页条数
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 组织 / 字典树最大深度
     */
    public static final int MAX_TREE_DEPTH = 16;
}