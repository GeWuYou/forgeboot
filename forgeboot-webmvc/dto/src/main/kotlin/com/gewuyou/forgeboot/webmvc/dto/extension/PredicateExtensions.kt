package com.gewuyou.forgeboot.webmvc.dto.extension

import jakarta.persistence.criteria.Predicate

/**
 * 在Predicate列表中添加条件，仅当字符串值不为空且不为空白时执行。
 *
 * @param value 待检查的字符串值
 * @param block 一个接受字符串并返回Predicate的lambda表达式
 */
inline fun MutableList<Predicate>.addIfNotBlank(
    value: String?,
    block: (String) -> Predicate
) {
    if (!value.isNullOrBlank()) {
        add(block(value))
    }
}

/**
 * 在Predicate列表中添加条件，仅当非字符串值不为空时执行。
 *
 * @param value 待检查的非字符串值
 * @param block 一个接受非字符串值并返回Predicate的lambda表达式
 */
inline fun <T> MutableList<Predicate>.addIfNotNull(
    value: T?,
    block: (T) -> Predicate
) {
    if (value != null) {
        add(block(value))
    }
}
