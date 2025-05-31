package com.gewuyou.forgeboot.webmvc.exception.i18n.handler


import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.i18n.api.MessageResolver
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.webmvc.dto.I18nResult
import com.gewuyou.forgeboot.webmvc.exception.i18n.config.entities.WebMvcExceptionI18nProperties
import com.gewuyou.forgeboot.webmvc.exception.i18n.core.GlobalException
import com.gewuyou.forgeboot.webmvc.exception.core.InternalException


import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * i18n全局异常处理类
 *
 * 该类用于统一处理整个应用中抛出的各种异常，以提供统一的错误响应格式
 * 它通过使用@RestControllerAdvice注解来标识这是一个全局异常处理类
 *
 * @author gewuyou
 * @since 2024-04-13 上午12:22:18
 */
@RestControllerAdvice
class GlobalExceptionHandler(
    private val webMvcExceptionI18nProperties: WebMvcExceptionI18nProperties,
    private val messageResolver: MessageResolver,
    private val requestIdProvider: RequestIdProvider,
) {
    /**
     * 异常处理器
     *
     * 用于处理除特定异常外的所有其他异常
     *
     * @param e 异常
     * @return 响应
     * @since 2024/4/13 上午12:29
     */
    @ExceptionHandler(Exception::class)
    fun handleOtherException(e: Exception): I18nResult<String> {
        log.error("other exception:", e)
        return I18nResult.failure(
            webMvcExceptionI18nProperties.otherGeneralExternalExceptionErrorCode,
            webMvcExceptionI18nProperties.otherGeneralExternalExceptionErrorMessage,
            null, null, messageResolver, requestIdProvider
        )
    }

    /**
     * 处理 @Valid 和 @Validated 校验失败抛出的 MethodArgumentNotValidException 异常
     *
     * 该方法首先尝试返回字段错误信息，如果没有字段错误则尝试返回全局错误信息，
     * 如果都没有则返回默认的校验异常信息
     *
     * @param ex 异常
     * @return 响应信息
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): I18nResult<String> {
        // 返回字段错误
        for (fieldError in ex.bindingResult.fieldErrors) {
            return I18nResult.failure(
                HttpStatus.BAD_REQUEST.value(),
                fieldError.defaultMessage ?: webMvcExceptionI18nProperties.defaultValidationExceptionFieldErrorMessage,
                null,
                null,
                messageResolver,
                requestIdProvider
            )
        }
        // 返回全局错误
        for (objectError in ex.bindingResult.globalErrors) {
            return I18nResult.failure(
                HttpStatus.BAD_REQUEST.value(),
                objectError.defaultMessage ?: webMvcExceptionI18nProperties.defaultValidationExceptionErrorMessage,
                null,
                null,
                messageResolver,
                requestIdProvider
            )
        }
        return I18nResult.failure(
            webMvcExceptionI18nProperties.defaultValidationExceptionErrorCode,
            webMvcExceptionI18nProperties.defaultValidationExceptionErrorMessage,
            null,
            null,
            messageResolver,
            requestIdProvider
        )
    }

    /**
     * 处理 JSR 303/JSR 380 校验失败抛出的 ConstraintViolationException 异常
     *
     * 该方法遍历约束违规信息，并返回第一个错误信息如果存在多个错误，
     * 否则返回默认的无效参数错误信息
     *
     * @param ex 异常
     * @return 响应信息
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): I18nResult<String> {
        for (constraintViolation in ex.constraintViolations) {
            return I18nResult.failure(
                HttpStatus.BAD_REQUEST.value(), constraintViolation.message,
                null, null, messageResolver, requestIdProvider
            )
        }
        return I18nResult.failure(
            webMvcExceptionI18nProperties.defaultInvalidParameterErrorCode,
            webMvcExceptionI18nProperties.defaultInvalidParameterErrorMessage,
            null,
            null,
            messageResolver,
            requestIdProvider
        )
    }

    /**
     * 全局异常处理器
     *
     * 用于处理全局异常（GlobalException），返回错误代码和国际化消息代码
     *
     * @param e 异常
     * @return 返回的结果
     * @since 2024/4/13 下午1:56
     */
    @ExceptionHandler(GlobalException::class)
    fun handleGlobalException(e: GlobalException): I18nResult<String> {
        return I18nResult.failure(
            e.errorCode, e.errorI18nMessageCode,
            null, e.errorI18nMessageArgs, messageResolver, requestIdProvider
        )
    }

    /**
     * 内部异常处理器
     *
     * 用于处理内部异常（InternalException），记录异常信息并返回默认的内部服务器错误信息
     *
     * @param e 异常
     * @return 返回的结果
     */
    @ExceptionHandler(InternalException::class)
    fun handleGlobalException(e: InternalException): I18nResult<String> {
        log.error("内部异常: 异常信息: {}", e.errorMessage, e)
        e.i18nInternalInformation?.responseI8nMessageCode?.let {
            log.error(
                "i18nMessage: {}", messageResolver.resolve(it, e.i18nInternalInformation.responseI8nMessageArgs)
            )
        }
        return I18nResult.failure(
            webMvcExceptionI18nProperties.defaultInternalServerErrorCode,
            webMvcExceptionI18nProperties.defaultInternalServerErrorMessage,
            null,
            null,
            messageResolver,
            requestIdProvider
        )
    }
}
