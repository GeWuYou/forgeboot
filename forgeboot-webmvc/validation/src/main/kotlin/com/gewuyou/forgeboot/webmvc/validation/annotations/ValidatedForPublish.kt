package com.gewuyou.forgeboot.webmvc.validation.annotations

import com.gewuyou.forgeboot.webmvc.validation.group.ValidationGroups
import org.springframework.validation.annotation.Validated

/**
 *Publish验证
 *
 * @since 2025-05-28 22:33:54
 * @author gewuyou
 */
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Validated(ValidationGroups.Publish::class)
annotation class ValidatedForPublish