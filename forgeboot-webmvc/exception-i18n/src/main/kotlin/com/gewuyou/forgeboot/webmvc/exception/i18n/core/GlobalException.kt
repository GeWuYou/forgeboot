package com.gewuyou.forgeboot.webmvc.exception.i18n.core

import com.gewuyou.forgeboot.i18n.api.I18nResponseInformation
import com.gewuyou.forgeboot.i18n.impl.exception.I18nBaseException


/**
 * i18n全局异常: 继承I18nBaseException，如果其它模块需要抛出全局异常，则继承此类
 *
 * @author gewuyou
 * @since 2024-11-23 16:45:10
 */
open class GlobalException : I18nBaseException {
    /**
     * 构造函数：初始化全局异常
     *
     * @param i18nResponseInformation 响应信息，包含错误代码和消息
     */
    constructor(i18nResponseInformation: I18nResponseInformation) : super(i18nResponseInformation)

    /**
     * 构造函数：初始化全局异常，并包含原始异常
     *
     * @param i18nResponseInformation 响应信息，包含错误代码和消息
     * @param cause               原始异常
     */
    constructor(i18nResponseInformation: I18nResponseInformation, cause: Throwable) : super(i18nResponseInformation, cause)
}
