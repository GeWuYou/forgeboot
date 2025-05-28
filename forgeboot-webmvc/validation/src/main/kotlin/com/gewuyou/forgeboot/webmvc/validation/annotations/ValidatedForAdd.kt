package com.gewuyou.forgeboot.webmvc.validation.annotations

import com.gewuyou.forgeboot.webmvc.validation.group.ValidationGroups
import org.springframework.validation.annotation.Validated

/**
 *Add验证
 *
 * @since 2025-05-28 22:31:13
 * @author gewuyou
 */
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Validated(ValidationGroups.Add::class)
annotation class ValidatedForAdd