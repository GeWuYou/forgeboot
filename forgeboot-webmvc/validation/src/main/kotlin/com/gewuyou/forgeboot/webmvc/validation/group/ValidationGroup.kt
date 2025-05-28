package com.gewuyou.forgeboot.webmvc.validation.group


/**
 * 验证分组接口集合
 *
 * 用于区分不同操作（添加、更新、删除）时的字段校验需求。
 * 可与 javax 或 jakarta validation 注解的 `groups` 属性配合使用。
 *
 * 示例用法：
 * ```kotlin
 * @NotNull(groups = [ValidationGroups.Update::class])
 * val id: Long
 * ```
 * - Add: 新增
 * - Update: 修改
 * - Delete: 删除
 * - Enable: 启用
 * - Disable: 禁用
 * - Publish: 发布
 * - Archive: 归档
 * @author gewuyou
 * @since 2025-01-18 14:32:41
 */
object ValidationGroups {

    /**
     * 添加操作的验证分组
     */
    interface Add

    /**
     * 更新操作的验证分组
     */
    interface Update

    /**
     * 删除操作的验证分组
     */
    interface Delete

    /**
     * 启用操作的验证分组
     */
    interface Enable

    /**
     * 启用操作的验证分组
     */
    interface Disable


    /**
     * 启用操作的验证分组
     */
    interface Publish

    /**
     * 归档操作的验证分组
     */
    interface Archive
}
