package com.gewuyou.forgeboot.webmvc.version.annotation

/**
 *API 版本注解
 * 可用于类或方法上，用于标识 API 的版本号。方法上的注解优先级高于类上的注解。
 * @since 2025-02-04 20:23:34
 * @author gewuyou
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiVersion (
    /**
     * API 版本号值 (如 v1, v2, v3 等)默认值为 v1
     */
    vararg val value: String = ["v1"]
)