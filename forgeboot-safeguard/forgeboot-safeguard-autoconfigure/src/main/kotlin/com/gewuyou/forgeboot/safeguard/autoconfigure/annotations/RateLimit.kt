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

package com.gewuyou.forgeboot.safeguard.autoconfigure.annotations

import com.gewuyou.forgeboot.safeguard.core.factory.RateLimitExceptionFactory
import kotlin.reflect.KClass

/**
 * 限流注解，用于标记需要进行令牌桶限流的方法
 * 该注解通过令牌桶算法实现接口调用频率控制，支持基于SpEL表达式的动态参数配置
 *
 * @property key 限流的唯一标识符，支持SpEL表达式，如"#userId+':'+#scene"
 * @property capacity 令牌桶容量，支持SpEL表达式，如"10"
 * @property refillTokens 每次填充的令牌数量，支持SpEL表达式，如"10"
 * @property refillPeriodMs 令牌填充周期(毫秒)，支持SpEL表达式，如"1000"
 * @property requested 每次请求消耗的令牌数，默认为1，支持SpEL表达式
 * @property template 限流异常信息模板
 * @property resolverBean 限流key解析器bean名称
 * @property refundOn 失败时触发令牌归还的异常类型数组，当方法抛出指定异常时会归还已消耗的令牌
 * @property scene 业务场景标识
 * @property infoCode 限流信息码
 * @property factory 限流异常工厂类
 * @property factoryBean 限流异常工厂bean名称
 * @since 2025-09-21 14:16:31
 * @author gewuyou
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RateLimit(
    val key: String,
    val capacity: String,
    val refillTokens: String,
    val refillPeriodMs: String,
    val requested: String = "1",
    val template: String = "",
    val resolverBean: String = "",
    val refundOn: Array<KClass<out Throwable>> = [], // 失败时归还许可
    val scene: String = "",
    val infoCode: String = "",
    val factory: KClass<out RateLimitExceptionFactory> =
        RateLimitExceptionFactory::class,
    val factoryBean: String = "",
)

