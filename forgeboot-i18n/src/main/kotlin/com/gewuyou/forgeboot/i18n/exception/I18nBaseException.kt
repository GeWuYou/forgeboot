package com.gewuyou.forgeboot.i18n.exception

import com.gewuyou.forgeboot.i18n.entity.ResponseInformation

/**
 * i18n异常
 *
 * @author gewuyou
 * @since 2024-11-12 00:11:32
 */
class I18nBaseException : RuntimeException {
    /**
     * 响应信息对象，用于存储错误代码和国际化消息代码
     */
    @Transient
    private val responseInformation: ResponseInformation

    /**
     * 构造函数
     *
     * @param responseInformation 响应信息对象，包含错误代码和国际化消息代码
     */
    constructor(responseInformation: ResponseInformation) : super() {
        this.responseInformation = responseInformation
    }

    /**
     * 构造函数
     *
     * @param responseInformation 响应信息对象，包含错误代码和国际化消息代码
     * @param cause               异常原因
     */
    constructor(responseInformation: ResponseInformation, cause: Throwable?) : super(cause) {
        this.responseInformation = responseInformation
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    val errorCode: Int
        get() = responseInformation.responseCode

    /**
     * 获取国际化消息代码
     *
     * @return 国际化消息代码
     */
    val errorI18nMessageCode: String
        get() = responseInformation.responseI8nMessageCode
}
