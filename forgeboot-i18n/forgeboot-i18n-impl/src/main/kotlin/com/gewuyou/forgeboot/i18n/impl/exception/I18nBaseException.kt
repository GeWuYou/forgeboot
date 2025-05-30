package com.gewuyou.forgeboot.i18n.impl.exception

import com.gewuyou.forgeboot.i18n.api.I18nResponseInformation


/**
 * i18n异常
 *
 * 该异常类用于处理国际化相关的异常情况，提供了错误代码和国际化消息代码的封装，
 * 以便于在异常处理时能够方便地获取这些信息
 *
 * @author gewuyou
 * @since 2024-11-12 00:11:32
 */
open class I18nBaseException : RuntimeException {
    /**
     * 响应信息对象，用于存储错误代码和国际化消息代码
     */
    @Transient
    private val i18nResponseInformation: I18nResponseInformation

    /**
     * 构造函数
     *
     * 初始化异常对象，用于当只有响应信息可用时
     *
     * @param i18nResponseInformation 响应信息对象，包含错误代码和国际化消息代码
     */
    constructor(i18nResponseInformation: I18nResponseInformation)
            : super(i18nResponseInformation.responseI8nMessageCode) {
        this.i18nResponseInformation = i18nResponseInformation
    }

    /**
     * 构造函数
     *
     * 初始化异常对象，用于当有响应信息和异常原因时
     *
     * @param i18nResponseInformation 响应信息对象，包含错误代码和国际化消息代码
     * @param cause               异常原因
     */
    constructor(i18nResponseInformation: I18nResponseInformation, cause: Throwable)
            : super(i18nResponseInformation.responseI8nMessageCode, cause) {
        this.i18nResponseInformation = i18nResponseInformation
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    val errorCode: Int
        get() = i18nResponseInformation.responseCode

    /**
     * 获取国际化消息代码
     *
     * @return 国际化消息代码
     */
    val errorI18nMessageCode: String
        get() = i18nResponseInformation.responseI8nMessageCode

    /**
     * 获取国际化消息参数
     *
     * @return 国际化消息参数数组
     */
    val errorI18nMessageArgs: Array<Any>?
        get() = i18nResponseInformation.responseI8nMessageArgs
}
