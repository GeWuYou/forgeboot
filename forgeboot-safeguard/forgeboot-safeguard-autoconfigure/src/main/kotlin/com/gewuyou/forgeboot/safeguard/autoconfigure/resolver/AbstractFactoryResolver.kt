/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.forgeboot.safeguard.autoconfigure.resolver

import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.ApplicationContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass


/**
 * 通用的工厂解析基类。
 *
 * F: 工厂接口类型（例如 AttemptLimitExceptionFactory）
 * A: 注解类型（例如 AttemptLimit）
 *
 * 解析顺序：
 * 1) 若注解声明 factoryBean（Bean 名），按名从容器获取
 * 2) 若注解 factory == sentinel（接口本身作哨兵），取容器默认/主 Bean，若无则用 defaultFallback()
 * 3) 否则按类型优先从容器拿，取不到再反射无参构造兜底
 * @since 2025-09-25 22:23:34
 * @author gewuyou
 * 结果按类型缓存（Bean 名获取不缓存；默认/主 Bean 也不缓存，避免替换时不生效）
 */
abstract class AbstractFactoryResolver<F : Any, A : Annotation>(
    private val applicationContext: ApplicationContext,
    private val defaultFactoryProvider: ObjectProvider<F>,
    private val sentinel: KClass<out F>,
    private val defaultFallback: () -> F,
) {

    private val cache = ConcurrentHashMap<KClass<out F>, F>()

    /**
     * 从注解中提取“工厂 Bean 名称”字段。
     *
     * @param annotation 当前处理的注解实例
     * @return 工厂 Bean 名称，若未指定则返回空字符串
     */
    protected abstract fun factoryBeanNameFrom(annotation: A): String

    /**
     * 从注解中提取“工厂类型”字段。
     *
     * @param annotation 当前处理的注解实例
     * @return 工厂类型的 KClass 表示
     */
    protected abstract fun factoryTypeFrom(annotation: A): KClass<out F>

    /**
     * 对已解析的工厂进行后处理（如注入模板、场景码等）。
     *
     * @param factory 已解析的工厂实例
     * @param annotation 当前处理的注解实例
     * @return 处理后的工厂实例，默认直接返回原实例
     */
    protected open fun postProcess(factory: F, annotation: A): F = factory

    /**
     * 根据注解解析出对应的工厂实例。
     *
     * @param annotation 当前处理的注解实例
     * @return 解析得到的工厂实例
     */
    fun resolve(annotation: A): F {
        // 1) Bean 名优先
        val beanName = factoryBeanNameFrom(annotation).trim()
        if (beanName.isNotEmpty()) {
            val bean = applicationContext.getBean(beanName, sentinel.java)
            return postProcess(bean, annotation)
        }

        // 2) 类型解析
        val type = factoryTypeFrom(annotation)

        // 2.1) 接口哨兵：取默认/主 Bean 或兜底
        if (type == sentinel) {
            val bean = defaultFactoryProvider.ifAvailable ?: defaultFallback()
            return postProcess(bean, annotation)
        }

        // 3) 显式类型：容器优先，其次反射；按类型缓存
        cache[type]?.let { return postProcess(it, annotation) }

        applicationContext.getBeanProvider(type.java).ifAvailable?.let {
            cache[type] = it
            return postProcess(it, annotation)
        }

        // 4) 反射兜底（要求无参构造）
        val inst = type.java.getDeclaredConstructor()
            .apply { isAccessible = true }
            .newInstance()
        cache[type] = inst
        return postProcess(inst, annotation)
    }
}
