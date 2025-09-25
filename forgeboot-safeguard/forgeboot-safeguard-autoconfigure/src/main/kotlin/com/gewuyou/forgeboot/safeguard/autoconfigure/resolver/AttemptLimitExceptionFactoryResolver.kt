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

import com.gewuyou.forgeboot.safeguard.autoconfigure.annotations.AttemptLimit
import com.gewuyou.forgeboot.safeguard.core.factory.AttemptLimitExceptionFactory
import com.gewuyou.forgeboot.safeguard.core.factory.DefaultAttemptLimitExceptionFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

/**
 * 尝试限制异常工厂解析器
 *
 * @param applicationContext Spring应用上下文，用于获取Bean实例
 * @param defaultFactoryProvider 默认异常工厂提供者，用于获取容器中的默认Bean
 * @since 2025-09-25 22:18:04
 * @author gewuyou
 */
class AttemptLimitExceptionFactoryResolver(
    applicationContext: ApplicationContext,
    defaultFactoryProvider: ObjectProvider<AttemptLimitExceptionFactory>,
) : AbstractFactoryResolver<AttemptLimitExceptionFactory, AttemptLimit>(
    applicationContext,
    defaultFactoryProvider,
    AttemptLimitExceptionFactory::class,
    { DefaultAttemptLimitExceptionFactory() }
) {
    /**
     * 从注解中提取“工厂 Bean 名称”字段。
     *
     * @param annotation 当前处理的注解实例
     * @return 工厂 Bean 名称，若未指定则返回空字符串
     */
    override fun factoryBeanNameFrom(annotation: AttemptLimit): String = annotation.factoryBean

    /**
     * 从注解中提取“工厂类型”字段。
     *
     * @param annotation 当前处理的注解实例
     * @return 工厂类型的 KClass 表示
     */
    override fun factoryTypeFrom(annotation: AttemptLimit): KClass<out AttemptLimitExceptionFactory> =
        annotation.factory

}
