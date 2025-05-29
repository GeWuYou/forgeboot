package com.gewuyou.forgeboot.core.extension

/**
 * 尝试执行一段代码块，如果执行过程中抛出异常，则返回null。
 * 使用inline避免额外的函数调用开销，对于性能敏感的场景特别有用。
 *
 * @param block 一个lambda表达式，代表尝试执行的代码块。
 * @return 成功执行的结果，或者如果执行过程中抛出异常，则返回null。
 */
inline fun <T> tryOrNull(block: () -> T): T? =
    try { block() } catch (_: Exception) { null }

/**
 * 尝试执行一段代码块，如果执行过程中抛出异常，则返回一个预设的备选值。
 * 同样使用inline关键字，以减少性能开销。
 *
 * @param fallBack 备选值，如果代码块执行过程中抛出异常，则返回该值。
 * @param block 一个lambda表达式，代表尝试执行的代码块。
 * @return 成功执行的结果，或者如果执行过程中抛出异常，则返回备选值。
 */
inline fun <T> tryOrFallBack(fallBack: T, block: () -> T): T =
    try { block() } catch (_: Exception) { fallBack }
