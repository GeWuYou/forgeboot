package com.gewuyou.forgeboot.context.impl.extension

import com.gewuyou.forgeboot.context.impl.ContextHolder

/**
 * 为ContextHolder实现赋值操作符的重写方法
 *
 * @param key 存储值的键，用于后续从上下文获取值时使用
 * @param value 需要存储在ContextHolder中的值，允许为null
 */
operator fun ContextHolder.set(key: String, value: Any?) {
    this.put(key, value)
}