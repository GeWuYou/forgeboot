package com.gewuyou.forgeboot.webmvc.validation.util

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validation
import jakarta.validation.Validator
import kotlin.collections.isNotEmpty

/**
 * 验证工具类
 *
 * 提供了数据对象的验证功能，使用Bean Validation规范
 * @since 2025-05-28 22:26:12
 * @author gewuyou
 */
class ValidationUtils {
    // 初始化默认的Validator实例
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * 验证对象是否符合规范
     *
     * @param obj 待验证的对象
     * @param groups 验证分组
     * @return 返回验证失败的约束违规集合
     */
    fun <T> validate(obj: T, vararg groups: Class<*>): Set<ConstraintViolation<T>> {
        return validator.validate(obj, *groups)
    }

    /**
     * 验证对象并抛出异常
     *
     * 如果对象验证不通过，则抛出ConstraintViolationException异常
     * @param obj 待验证的对象
     * @param groups 验证分组
     * @throws ConstraintViolationException 如果存在验证错误
     */
    fun <T> validateAndThrow(obj: T, vararg groups: Class<*>) {
        val violations = validate(obj, *groups)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }
}
