package com.gewuyou.forgeboot.webmvc.validation.annotations

import com.gewuyou.forgeboot.webmvc.validation.group.ValidationGroups
import org.springframework.validation.annotation.Validated

/**
 *Update验证
 *
 * @since 2025-05-28 22:31:49
 * @author gewuyou
 */
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Validated(ValidationGroups.Update::class)
annotation class ValidatedForUpdate