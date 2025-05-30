package com.gewuyou.forgeboot.webmvc.exception.handler


import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.webmvc.dto.R
import com.gewuyou.forgeboot.webmvc.exception.config.entities.WebMvcExceptionProperties
import com.gewuyou.forgeboot.webmvc.exception.core.GlobalException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 全局异常处理类
 *
 * 该类用于统一处理整个应用中抛出的各种异常，以提供统一的错误响应格式
 * 它通过使用@RestControllerAdvice注解来标识这是一个全局异常处理类
 *
 * @author gewuyou
 * @since 2024-04-13 上午12:22:18
 */
@RestControllerAdvice
class GlobalExceptionHandler(
    private val webMvcExceptionProperties: WebMvcExceptionProperties,
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
    fun handleOtherException(e: Exception): R<String> {
        log.error("other exception:", e)
        return R.failure(
            webMvcExceptionProperties.otherGeneralExternalExceptionErrorCode.toString(),
            webMvcExceptionProperties.otherGeneralExternalExceptionErrorMessage,
            null, requestIdProvider
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
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): R<String> {
        // 返回字段错误
        for (fieldError in ex.bindingResult.fieldErrors) {
            return R.failure(
                HttpStatus.BAD_REQUEST.value().toString(),
                fieldError.defaultMessage ?: webMvcExceptionProperties.defaultValidationExceptionFieldErrorMessage,
                null,
                requestIdProvider
            )
        }
        // 返回全局错误
        for (objectError in ex.bindingResult.globalErrors) {
            return R.failure(
                HttpStatus.BAD_REQUEST.value().toString(),
                objectError.defaultMessage ?: webMvcExceptionProperties.defaultValidationExceptionErrorMessage,
                null,
                requestIdProvider
            )
        }
        return R.failure(
            webMvcExceptionProperties.defaultValidationExceptionErrorCode.toString(),
            webMvcExceptionProperties.defaultValidationExceptionErrorMessage,
            null,
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
    fun handleConstraintViolationException(ex: ConstraintViolationException): R<String> {
        for (constraintViolation in ex.constraintViolations) {
            return R.failure(
                HttpStatus.BAD_REQUEST.value().toString(), constraintViolation.message,
                null, requestIdProvider
            )
        }
        return R.failure(
            webMvcExceptionProperties.defaultInvalidParameterErrorCode.toString(),
            webMvcExceptionProperties.defaultInvalidParameterErrorMessage,
            null,
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
    fun handleGlobalException(e: GlobalException): R<String> {
        return R.failure(
            e.responseInformation.responseStateCode(),
            e.responseInformation.responseMessage(),
            null, requestIdProvider
        )
    }
}
