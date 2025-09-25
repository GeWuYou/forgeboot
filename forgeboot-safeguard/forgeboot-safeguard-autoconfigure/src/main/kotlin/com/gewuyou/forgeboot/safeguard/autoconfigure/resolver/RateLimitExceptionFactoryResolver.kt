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

import com.gewuyou.forgeboot.safeguard.autoconfigure.annotations.RateLimit
import com.gewuyou.forgeboot.safeguard.core.factory.DefaultRateLimitExceptionFactory
import com.gewuyou.forgeboot.safeguard.core.factory.RateLimitExceptionFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

/**
 * 限流异常工厂解析器
 *
 * @since 2025-09-25 22:30:07
 * @author gewuyou
 */
class RateLimitExceptionFactoryResolver(
    /**
     * 应用上下文，用于获取Spring容器中的Bean
     */
    applicationContext: ApplicationContext,
    /**
     * 默认工厂提供者，用于获取默认的限流异常工厂
     */
    defaultFactoryProvider: ObjectProvider<RateLimitExceptionFactory>,
) : AbstractFactoryResolver<RateLimitExceptionFactory, RateLimit>(
    applicationContext,
    defaultFactoryProvider,
    RateLimitExceptionFactory::class,
    { DefaultRateLimitExceptionFactory() }
) {
    /**
     * 从注解中提取"工厂 Bean 名称"字段。
     *
     * @param annotation 当前处理的注解实例
     * @return 工厂 Bean 名称，若未指定则返回空字符串
     */
    override fun factoryBeanNameFrom(annotation: RateLimit): String = annotation.factoryBean

    /**
     * 从注解中提取"工厂类型"字段。
     *
     * @param annotation 当前处理的注解实例
     * @return 工厂类型的 KClass 表示
     */
    override fun factoryTypeFrom(annotation: RateLimit): KClass<out RateLimitExceptionFactory> = annotation.factory
}
