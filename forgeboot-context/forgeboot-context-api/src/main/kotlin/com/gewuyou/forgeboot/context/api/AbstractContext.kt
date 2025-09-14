/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.forgeboot.context.api

/**
 *抽象上下文
 *
 * @since 2025-06-04 13:36:54
 * @author gewuyou
 */
abstract class AbstractContext<K, V> : Context<K, V> {
    /**
     * 获取当前线程的本地存储映射表
     *
     * @return 返回与当前线程关联的可变映射表，用于存储键值对数据
     */
    private val local = ThreadLocal.withInitial { mutableMapOf<K, V>() }

    /**
     * 获取底层的可变映射表
     *
     * @return 返回当前线程本地存储中的可变映射表实例
     */
    protected fun backing(): MutableMap<K, V> = local.get()

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
     * 从上下文中移除指定的键值对并返回被移除的值。
     *
     * 此方法用于在上下文中删除与指定键关联的条目。如果该键存在，
     * 则将其从上下文中移除，并返回与之关联的值；如果该键不存在，
     * 则返回 null。
     *
     * @param key 要移除的键，不能为空
     * @return 与指定键关联的值，如果键不存在则返回 null
     */
    override fun remove(key: K): V? {
        return local.get().remove(key)
    }

    /**
     * 将指定映射中的所有键值对设置到本地存储中，忽略值为null的条目
     * @param map 包含要设置的键值对的映射，值可以为null
     */
    override fun setAll(map: Map<K, V?>) {
        val b = mutableMapOf<K, V>()
        map.forEach { (k, v) -> if (v != null) b[k] = v }
        local.set(b)
    }

    /**
     * 获取所有键的集合
     * @return 包含所有键的不可变集合
     */
    override fun keys(): Set<K> = backing().keys.toSet()

    /**
     * 检查指定键是否存在于映射中
     * @param key 要检查的键
     * @return 如果键存在则返回true，否则返回false
     */
    override fun containsKey(key: K): Boolean = backing().containsKey(key)

    /**
     * 检查映射是否为空
     * @return 如果映射中没有键值对则返回true，否则返回false
     */
    override fun isEmpty(): Boolean = backing().isEmpty()

    /**
     * 获取映射中键值对的数量
     * @return 映射中键值对的数量
     */
    override fun size(): Int = backing().size

    /**
     * 获取指定键对应的值，如果键不存在则返回默认值
     * @param key 要获取值的键
     * @param defaultValue 当键不存在时返回的默认值
     * @return 键对应的值或默认值
     */
    override fun getOrDefault(key: K, defaultValue: V): V =
        retrieve(key) ?: defaultValue

    /**
     * 如果指定键不存在或其值为null，则使用提供的供应函数计算新值并存储
     * @param key 要检查的键
     * @param supplier 用于计算新值的供应函数
     * @return 键对应的现有值或新计算的值
     */
    override fun computeIfAbsent(key: K, supplier: () -> V): V {
        val b = backing()
        val cur = b[key]
        if (cur != null) return cur
        val computed = supplier()
        // 与 put 保持一致：不存 null
        b[key] = computed
        return computed
    }

}