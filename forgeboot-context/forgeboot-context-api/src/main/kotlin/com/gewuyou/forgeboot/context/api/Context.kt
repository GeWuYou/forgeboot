package com.gewuyou.forgeboot.context.api

/**
 * 上下文接口，用于管理键值对的存储、获取和清理操作。
 *
 * @since 2025-06-04 13:34:04
 * @author gewuyou
 */
interface Context<K, V> {
    /**
     * 将指定的键值对存入上下文中。
     *
     * @param key   要存储的键
     * @param value 要存储的值，可以为 null
     */
    fun put(key: K, value: V?)

    /**
     * 根据指定的键从上下文中获取对应的值。
     *
     * @param key 要查找的键
     * @return 对应的值，如果不存在则返回 null
     */
    fun retrieve(key: K): V?

    /**
     * 根据指定的键和类型从上下文中获取对应的值。
     *
     * @param key  要查找的键
     * @param type 要转换的目标类型
     * @return 对应类型的值，如果不存在或类型不匹配则返回 null
     */
    fun <T> retrieveByType(key: K, type: Class<T>): T?

    /**
     * 获取当前上下文的一个快照，包含所有键值对。
     *
     * @return 一个 Map，表示当前上下文中的所有键值对
     */
    fun snapshot(): Map<K, V?>

    /**
     * 清除上下文中的所有键值对。
     */
    fun clear()

    /**
     * 从上下文中移除指定的键值对并返回被移除的值。
     *
     * 此方法用于在上下文中删除与指定键关联的条目。如果该键存在，
     * 则将其从上下文中移除，并返回与之关联的值；如果该键不存在，
     * 则返回 null。
     *
     * @param key 要移除的键，不能为空
     * @return 与指定键关联的值，如果键不存在则返回 null
     */
    fun remove(key: K): V?
}