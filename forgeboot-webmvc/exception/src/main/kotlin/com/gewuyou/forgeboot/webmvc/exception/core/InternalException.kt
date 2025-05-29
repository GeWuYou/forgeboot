package com.gewuyou.forgeboot.webmvc.exception.core

import com.gewuyou.forgeboot.i18n.api.InternalInformation


/**
 * 内部异常
 *
 * @author gewuyou
 * @since 2024-11-24 21:14:03
 */
open class InternalException : RuntimeException {
    /**
     * 错误信息
     */
    val errorMessage: String

    /**
     * 可选(国际化内部错误信息码)
     */
    @Transient
    val internalInformation: InternalInformation?

    /**
     * 构造一个新的运行时异常，其详细消息为`null`。
     * 原因尚未初始化，可以随后通过调用[.initCause]进行初始化。
     *
     * @param errorMessage 详细消息
     */
    constructor(errorMessage: String) {
        this.errorMessage = errorMessage
        this.internalInformation = null
    }

    /**
     * 使用指定的详细消息和原因构造新的运行时异常。
     *
     * @param errorMessage 详细消息
     * @param cause         异常的原因
     */
    constructor(errorMessage: String, cause: Throwable?) : super(errorMessage, cause) {
        this.errorMessage = errorMessage
        this.internalInformation = null
    }

    /**
     * 使用指定的详细消息和国际化内部错误信息码构造新的运行时异常。
     *
     * @param errorMessage      详细消息
     * @param internalInformation 国际化内部错误信息码
     */
    constructor(errorMessage: String, internalInformation: InternalInformation?) {
        this.errorMessage = errorMessage
        this.internalInformation = internalInformation
    }

    /**
     * 使用指定的详细消息、原因和国际化内部错误信息码构造新的运行时异常。
     *GlobalExceptionHandler
     * @param errorMessage      详细消息
     * @param cause             异常的原因
     * @param internalInformation 国际化内部错误信息码
     */
    constructor(errorMessage: String, cause: Throwable?, internalInformation: InternalInformation?) : super(
        errorMessage,
        cause
    ) {
        this.errorMessage = errorMessage
        this.internalInformation = internalInformation
    }
}
