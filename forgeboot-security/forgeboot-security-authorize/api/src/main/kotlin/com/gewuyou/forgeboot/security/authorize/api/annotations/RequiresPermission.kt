package com.gewuyou.forgeboot.security.authorize.api.annotations

/**
 * 权限校验注解，用于标记需要特定权限才能访问的方法
 *
 * @property value 需要校验的权限字符串，例如："user:read"
 * @property dynamic 是否为动态权限，默认为 false。若为 true 表示该权限需要在运行时动态解析
 *
 * 注解作用于方法级别，运行时生效，常用于接口权限控制。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresPermission(
    val value: String,
    val dynamic: Boolean = false
)