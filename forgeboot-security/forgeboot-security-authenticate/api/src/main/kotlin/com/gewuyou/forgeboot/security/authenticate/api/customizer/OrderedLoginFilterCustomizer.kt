package com.gewuyou.forgeboot.security.authenticate.api.customizer


/**
 * 接口用于支持登录过滤器扩展器的排序功能。
 *
 * 此接口继承自 [LoginFilterCustomizer]，通过提供一个 order 属性来定义实现类的执行顺序，
 * 确保多个 [LoginFilterCustomizer] 实现类能够按照预期的顺序被调用。
 *
 * @since 2025-06-27 07:58:46
 * @author gewuyou
 */
interface OrderedLoginFilterCustomizer : LoginFilterCustomizer {
    /**
     * 定义当前定制器的执行顺序。
     *
     * 数值越小，优先级越高，即会越早被执行。
     */
    val order: Int
}