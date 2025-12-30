package com.gewuyou.forgeboot.webmvc.exception.impl

import com.gewuyou.forgeboot.webmvc.dto.api.entities.Failure
import com.gewuyou.forgeboot.webmvc.dto.api.entities.SimpleInfo
import com.gewuyou.forgeboot.webmvc.dto.impl.Responses
import jakarta.validation.ConstraintViolationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 校验异常处理器
 *
 * @since 2025-12-30 12:04:15
 * @author gewuyou
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ValidationExceptionAdvice {

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
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): Failure {
        // 返回字段错误
        for (fieldError in ex.bindingResult.fieldErrors) {
            return Responses.fail(
                info = SimpleInfo(
                    HttpStatus.BAD_REQUEST.value(), fieldError.defaultMessage ?: "field verification error"
                )
            )
        }
        // 返回全局错误
        for (objectError in ex.bindingResult.globalErrors) {
            return Responses.fail(
                info = SimpleInfo(
                    HttpStatus.BAD_REQUEST.value(), objectError.defaultMessage ?: "object verification error"
                )
            )
        }
        return Responses.fail(
            info = SimpleInfo(
                HttpStatus.BAD_REQUEST.value(), "verification error"
            )
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
    fun handleConstraintViolationException(ex: ConstraintViolationException): Failure {
        for (constraintViolation in ex.constraintViolations) {
            return Responses.fail(
                info = SimpleInfo(
                    HttpStatus.BAD_REQUEST.value(), constraintViolation.message
                )
            )
        }
        return Responses.fail(
            info = SimpleInfo(
                HttpStatus.BAD_REQUEST.value(), "constraint verification error"
            )
        )
    }
}
