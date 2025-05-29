package com.gewuyou.forgeboot.webmvc.exception.core

import com.gewuyou.forgeboot.i18n.api.ResponseInformation
import com.gewuyou.forgeboot.i18n.impl.exception.I18nBaseException


/**
 * 全局异常: 继承I18nBaseException，如果其它模块需要抛出全局异常，则继承此类
 *
 * @author gewuyou
 * @since 2024-11-23 16:45:10
 */
open class GlobalException : I18nBaseException {
    /**
     * 构造函数：初始化全局异常
     *
     * @param responseInformation 响应信息，包含错误代码和消息
     */
    constructor(responseInformation: ResponseInformation) : super(responseInformation)

    /**
     * 构造函数：初始化全局异常，并包含原始异常
     *
     * @param responseInformation 响应信息，包含错误代码和消息
     * @param cause               原始异常
     */
    constructor(responseInformation: ResponseInformation, cause: Throwable) : super(responseInformation, cause)
}
