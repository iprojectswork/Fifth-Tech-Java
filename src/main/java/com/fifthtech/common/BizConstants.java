package com.fifthtech.common;

/**
 * BizConstants
 *
 * @author RH
 * @description 业务常量（禁止在业务代码里写裸 0/1 等魔法值）
 * @date 2026-08-15
 */
public final class BizConstants {

    private BizConstants() {
    }

    /** 禁用 / 停用 */
    public static final int STATUS_DISABLED = 0;

    /** 启用 */
    public static final int STATUS_ENABLED = 1;

    /** 未逻辑删除 */
    public static final int NOT_DELETED = 0;

    /** 已逻辑删除 */
    public static final int DELETED = 1;

    /** 树根的 parent_id */
    public static final long ROOT_PARENT_ID = 0L;

    /** 权限类型：菜单 */
    public static final int PERMISSION_TYPE_MENU = 1;

    /** 权限类型：按钮 */
    public static final int PERMISSION_TYPE_BUTTON = 2;

    /** 默认页码 */
    public static final int DEFAULT_PAGE_CURRENT = 1;

    /** 默认每页条数 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** 组织 / 字典树最大深度 */
    public static final int MAX_TREE_DEPTH = 16;
}
