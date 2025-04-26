package com.gewuyou.forgeboot.webmvc.logger.annotation

/**
 * 记录方法的注解： 记录出参入参，方法执行耗时等信息
 *
 * @since 2025-01-23 14:22:11
 * @author gewuyou
 */
// 注解用于函数
@Target(AnnotationTarget.FUNCTION)
// 在运行时可用
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MethodRecording(val description: String = "",val printArgs: Boolean = true,val printResult: Boolean = true,val printTime: Boolean = true)