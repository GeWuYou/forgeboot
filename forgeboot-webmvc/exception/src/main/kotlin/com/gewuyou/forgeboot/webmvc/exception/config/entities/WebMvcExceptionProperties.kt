package com.gewuyou.forgeboot.webmvc.exception.config.entities

import com.gewuyou.forgeboot.webmvc.extension.i18n.I18nKeys
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Web Mvc异常属性
 *
 * @author gewuyou
 * @since 2025-05-13 11:06:46
 */
@ConfigurationProperties("forgeboot.webmvc.exception")
class WebMvcExceptionProperties {
    /**
     * 设置其他通用外部异常的错误代码
     *
     * @param otherGeneralExternalExceptionErrorCode 其他通用外部异常的错误代码
     */
    var otherGeneralExternalExceptionErrorCode: Int = 500


    /**
     * 设置其他通用外部异常的错误消息
     *
     * @param otherGeneralExternalExceptionErrorMessage 其他通用外部异常的错误消息
     */
    var otherGeneralExternalExceptionErrorMessage: String = I18nKeys.Forgeboot.Webmvc.Exception.INVALID_SERVER_ERROR

    /**
     * 设置默认验证异常的错误代码
     *
     * @param defaultValidationExceptionErrorCode 默认验证异常的错误代码
     */
    var defaultValidationExceptionErrorCode: Int = 400


    /**
     * 设置默认验证异常的错误消息
     *
     * @param defaultValidationExceptionErrorMessage 默认验证异常的错误消息
     */
    var defaultValidationExceptionErrorMessage: String = I18nKeys.Forgeboot.Webmvc.Exception.ILLEGAL_PARAMETER

    /**
     * 设置默认验证异常的字段错误消息
     *
     * @param defaultValidationExceptionFieldErrorMessage 默认验证异常的字段错误消息
     */
    var defaultValidationExceptionFieldErrorMessage: String = I18nKeys.Forgeboot.Webmvc.Exception.ILLEGAL_PARAMETER

    /**
     * 设置默认无效参数异常的错误代码
     *
     * @param defaultInvalidParameterErrorCode 默认无效参数异常的错误代码
     */
    var defaultInvalidParameterErrorCode: Int = 400

    /**
     * 设置默认无效参数异常的错误消息
     *
     * @param defaultInvalidParameterErrorMessage 默认无效参数异常的错误消息
     */
    var defaultInvalidParameterErrorMessage: String = I18nKeys.Forgeboot.Webmvc.Exception.ILLEGAL_PARAMETER

    /**
     * 设置默认内部服务器错误异常的错误代码
     *
     * @param defaultInternalServerErrorCode 默认内部服务器错误异常的错误代码
     */
    var defaultInternalServerErrorCode: Int = 500


    /**
     * 设置默认内部服务器错误异常的错误消息
     *
     * @param defaultInternalServerErrorMessage 默认内部服务器错误异常的错误消息
     */
    var defaultInternalServerErrorMessage: String = I18nKeys.Forgeboot.Webmvc.Exception.INVALID_SERVER_ERROR
}
