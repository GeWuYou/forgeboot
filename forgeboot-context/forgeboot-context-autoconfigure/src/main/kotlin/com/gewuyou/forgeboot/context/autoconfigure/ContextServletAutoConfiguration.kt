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

package com.gewuyou.forgeboot.context.autoconfigure

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.impl.filter.ContextServletFilter
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

/**
 * 上下文 Servlet 自动配置类
 *
 * 该配置类用于在基于 Servlet 的 Web 应用中自动装配上下文传播相关的组件。
 * 主要功能包括：
 * - 在满足 Servlet 环境条件时启用配置
 * - 注册 ContextServletFilter Bean 以支持请求链中的上下文一致性维护
 *
 * @since 2025-06-24 22:15:23
 * @author gewuyou
 */
@AutoConfiguration
@ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "servlet", matchIfMissing = true)
@ConditionalOnClass(name = ["jakarta.servlet.Filter"])
class ContextServletAutoConfiguration {

    /**
     * 构建并注册 ContextServletFilter 实例
     *
     * 此方法创建一个用于同步阻塞的 Servlet 请求链中的上下文传播过滤器。
     * 只有在容器中尚未存在相同类型的 Bean 时才会注册。
     *
     * @param chain 处理器链，包含多个 ContextProcessor 实例
     * @return 构建完成的 ContextServletFilter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    fun contextServletFilter(chain: List<ContextProcessor>) =
        ContextServletFilter(chain)
}