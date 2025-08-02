package com.gewuyou.forgeboot.core.extension

/**
 * 尝试执行一段代码块，如果执行过程中抛出异常，则返回null。
 * 使用inline避免额外的函数调用开销，对于性能敏感的场景特别有用。
 *
 * @param T 泛型参数，表示代码块执行的返回类型
 * @param block 一个lambda表达式，代表尝试执行的代码块
 * @return T? 成功执行的结果，或者如果执行过程中抛出异常，则返回null
 */
inline fun <T> tryOrNull(block: () -> T): T? =
    try {
        block()
    } catch (_: Exception) {
        null
    }

/**
 * 尝试执行一段代码块，如果执行过程中抛出异常，则返回一个预设的备选值。
 * 同样使用inline关键字，以减少性能开销。
 *
 * @param T 泛型参数，表示代码块执行的返回类型和备选值类型
 * @param defaultValue 备选值，如果代码块执行过程中抛出异常，则返回该值
 * @param block 一个lambda表达式，代表尝试执行的代码块
 * @return T 成功执行的结果，或者如果执行过程中抛出异常，则返回备选值
 */
inline fun <T> tryOrFallBack(defaultValue: T, block: () -> T): T =
    try {
        block()
    } catch (_: Exception) {
        defaultValue
    }


/**
 * 检查给定的条件是否为真，若为假则使用指定的异常工厂函数创建并抛出异常。
 *
 * @param T 异常类型，必须继承自 Throwable
 * @param value 要检查的布尔值，如果为 false 则会使用exceptionFactory创建并抛出异常
 * @param exceptionFactory 用于创建异常实例的工厂函数，接收一个字符串消息参数
 * @param lazyMessage 延迟计算的消息提供函数，当需要抛出异常时调用并转换为字符串作为异常消息
 * @throws T 当value为false时，通过exceptionFactory创建的异常类型
 */
inline fun <T : Throwable> requireX(
    value: Boolean,
    exceptionFactory: (String) -> T,
    lazyMessage: () -> Any,
) {
    if (!value) throw exceptionFactory(lazyMessage().toString())
}

/**
 * 检查给定的条件是否为真，若为假则使用指定的异常类型抛出带有延迟生成消息的异常。
 *
 * @param T 异常类型，必须继承自 Throwable，并且需要有仅接受单个 String 参数的构造函数
 * @param condition 要检查的布尔值，如果为 false 则会抛出异常
 * @param lazyMessage 延迟计算的消息提供函数，当需要抛出异常时调用
 * @throws IllegalArgumentException 如果指定的异常类型没有合适的单参数字符串构造函数
 */
inline fun <reified T : Throwable> checkOrThrow(
    condition: Boolean,
    lazyMessage: () -> Any,
) {
    if (!condition) {
        val constructor = T::class.constructors.firstOrNull {
            it.parameters.size == 1 && it.parameters[0].type.classifier == String::class
        } ?: throw IllegalArgumentException("Exception type must have a single String parameter constructor")
        throw constructor.call(lazyMessage().toString())
    }
}

/**
 * 条件检查失败时，通过异常工厂函数创建异常并抛出。
 *
 * @param T 异常类型，必须继承自 Throwable
 * @param condition 要检查的布尔值，如果为 false 则会使用exceptionFactory创建并抛出异常
 * @param exceptionFactory 用于创建异常实例的工厂函数，接收一个字符串消息参数
 * @param lazyMessage 延迟计算的消息提供函数，当需要抛出异常时调用并转换为字符串作为异常消息
 */
inline fun <T : Throwable> checkOrThrowWithFactory(
    condition: Boolean,
    exceptionFactory: (String) -> T,
    lazyMessage: () -> Any,
) {
    if (!condition) {
        throw exceptionFactory(lazyMessage().toString())
    }
}

/**
 * 条件检查失败时，直接抛出指定的异常。
 *
 * @param condition 要检查的布尔值，如果为 false 则会调用exception函数并抛出其返回的异常
 * @param exception 提供要抛出的 Throwable 实例的函数
 */
inline fun checkOrThrowDirect(
    condition: Boolean,
    exception: () -> Throwable,
) {
    if (!condition) {
        throw exception()
    }
}