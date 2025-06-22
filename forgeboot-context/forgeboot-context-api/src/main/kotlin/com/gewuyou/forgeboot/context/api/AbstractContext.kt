package com.gewuyou.forgeboot.context.api

/**
 *抽象上下文
 *
 * @since 2025-06-04 13:36:54
 * @author gewuyou
 */
abstract class AbstractContext<K, V>:  Context<K, V> {
    private val local= ThreadLocal.withInitial { mutableMapOf<K,V>() }
    /**
     * 将指定的键值对存入上下文中。
     *
     * @param key   要存储的键
     * @param value 要存储的值，可以为 null
     */
    override fun put(key: K, value: V?) {
        value?.let {
            local.get()[key] = it
        }
    }

    /**
     * 根据指定的键从上下文中获取对应的值。
     *
     * @param key 要查找的键
     * @return 对应的值，如果不存在则返回 null
     */
    override fun retrieve(key: K): V? {
        return local.get()[key]
    }

    /**
     * 获取当前上下文的一个快照，包含所有键值对。
     *
     * @return 一个 Map，表示当前上下文中的所有键值对
     */
    override fun snapshot(): Map<K, V> {
        return HashMap(local.get())
    }

    /**
     * 清除上下文中的所有键值对。
     */
    override fun clear() {
        local.remove()
    }

    /**
     * 从上下文中移除指定的键值对。
     *
     * @param key 要移除的键
     */
    override fun remove(key: K) {
        local.get().remove(key)
    }
}